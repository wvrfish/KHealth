/*
 * Copyright (c) 2024 Shubham Singh
 *
 * This library is licensed under the Apache 2.0 License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khealth

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.Period
import kotlin.math.floor
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

actual class KHealth {
    constructor(activity: ComponentActivity) {
        this.activity = activity
        this.coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        this.permissionsChannel = Channel()
    }

    internal constructor(
        client: HealthConnectClient,
        coroutineScope: CoroutineScope,
        isHealthStoreAvailable: Boolean,
        permissionsChannel: Channel<Set<String>>
    ) {
        this.client = client
        this.coroutineScope = coroutineScope
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
        this.permissionsChannel = permissionsChannel
        this.isTestMode = true
    }

    private var activity: ComponentActivity? = null

    private lateinit var client: HealthConnectClient
    private val coroutineScope: CoroutineScope
    private var testIsHealthStoreAvailable: Boolean? = null
    private val permissionsChannel: Channel<Set<String>>
    private var isTestMode = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Set<String>>

    actual fun initialise() {
        if (!::client.isInitialized) client = HealthConnectClient.getOrCreate(activity!!)
        if (!::permissionsLauncher.isInitialized) {
            val permissionContract = PermissionController.createRequestPermissionResultContract()
            permissionsLauncher = activity!!.registerForActivityResult(permissionContract) {
                coroutineScope.launch {
                    permissionsChannel.send(it)
                }
            }
        }
    }

    actual val isHealthStoreAvailable: Boolean
        get() = testIsHealthStoreAvailable
            ?: (HealthConnectClient.getSdkStatus(activity!!) == HealthConnectClient.SDK_AVAILABLE)

    private fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    actual suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        verifyHealthStoreAvailability()
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        verifyHealthStoreAvailability()
        val permissionSets = permissions.map { entry -> entry.toPermissions() }

        if (::permissionsLauncher.isInitialized) {
            permissionsLauncher.launch(permissionSets.flatten().map { it.first }.toSet())
        } else if (!isTestMode) {
            logError(
                throwable = HealthStoreNotInitialisedException,
                methodName = "requestPermissions"
            )
        }

        val grantedPermissions = permissionsChannel.receive()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun writeRecords(vararg records: KHRecord): KHWriteResponse {
        try {
            verifyHealthStoreAvailability()
            val hcRecords = records.mapNotNull { record -> record.toHCRecord() }
            logDebug("Inserting ${hcRecords.size} records...")
            val responseIDs = client.insertRecords(hcRecords).recordIdsList
            logDebug("Inserted ${responseIDs.size} records")
            return when {
                responseIDs.size != hcRecords.size && responseIDs.isEmpty() -> {
                    KHWriteResponse.Failed(Exception("No records were written!"))
                }

                responseIDs.size != hcRecords.size -> KHWriteResponse.SomeFailed

                else -> KHWriteResponse.Success
            }
        } catch (t: Throwable) {
            val parsedThrowable = when (t) {
                is SecurityException -> NoWriteAccessException(t.message?.extractHealthPermission())
                else -> t
            }
            logError(throwable = t, methodName = "writeRecords")
            return KHWriteResponse.Failed(parsedThrowable)
        }
    }

    actual suspend fun readRecords(request: KHReadRequest): List<KHRecord> {
        return try {
            val recordClass = request.toRecordClass() ?: return emptyList()

            val hcRecords = client.readRecords(
                request = ReadRecordsRequest(
                    recordType = recordClass,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = request.startDateTime.toJavaInstant(),
                        endTime = request.endDateTime.toJavaInstant()
                    ),
                )
            ).records

            hcRecords.mapNotNull { record -> record.toKHRecordOrNull(request) }
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "readRecords")
            emptyList()
        }
    }

    actual suspend fun readWorkoutRecords(request: KHReadRequest.ExerciseFull): List<KHRecord.ExerciseFull> {
        return try {
            val hcRecords = client.readRecords(
                request = ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = request.startDateTime.toJavaInstant(),
                        endTime = request.endDateTime.toJavaInstant()
                    ),
                )
            ).records

            val records = mutableListOf<KHRecord.ExerciseFull>()
            for (record in hcRecords) {

                val response = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(
                            ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                            DistanceRecord.DISTANCE_TOTAL,
                            HeartRateRecord.BPM_AVG
                        ),
                        timeRangeFilter = TimeRangeFilter.between(
                            record.startTime,
                            record.endTime
                        )
                    )
                )
                val activeCals =
                    response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories
                val distance = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters
                val avgHR = response[HeartRateRecord.BPM_AVG]

                val duration: Duration =
                    record.endTime.toKotlinInstant() - record.startTime.toKotlinInstant()
                val secs = duration.inWholeMilliseconds.toDouble() / 1000
                val slice =
                    max(request.minimumSliceSecs, floor(secs / request.targetHeartRateSamples))
                val aggHR: List<KHHeartRateRangeSample>? = client.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = setOf(
                            HeartRateRecord.BPM_AVG,
                            HeartRateRecord.BPM_MAX,
                            HeartRateRecord.BPM_MIN
                        ),
                        timeRangeFilter = TimeRangeFilter.between(
                            startTime = record.startTime,
                            endTime = record.endTime
                        ),
                        timeRangeSlicer = slice.seconds.toJavaDuration()
                    )
                ).mapNotNull {
                    val max = it.result[HeartRateRecord.BPM_MAX]?.toDouble()
                    val min = it.result[HeartRateRecord.BPM_MIN]?.toDouble()
                    val avg = it.result[HeartRateRecord.BPM_AVG]?.toDouble()
                    if (max != null && min != null && avg != null) {
                        KHHeartRateRangeSample(
                            startTime = it.startTime.toKotlinInstant(),
                            endTime = it.endTime.toKotlinInstant(),
                            maxBeatsPerMinute = max,
                            minBeatsPerMinute = min,
                            averageBeatsPerMinute = avg
                        )
                    } else {
                        null
                    }
                }.ifEmpty { null }

                val type = record.exerciseType.toKHExerciseTypeOrNull()
                if (type != null) {
                    records.add(
                        KHRecord.ExerciseFull(
                            type = type,
                            startTime = record.startTime.toKotlinInstant(),
                            endTime = record.endTime.toKotlinInstant(),
                            activeCaloriesBurned = activeCals,
                            distanceCovered = distance,
                            averageHeartRate = avgHR?.toDouble(),
                            heartRateSamples = aggHR
                        )
                    )
                }


            }
            return records
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "readWorkoutRecords")
            emptyList()
        }
    }

    actual suspend fun aggregatedDailyReadRecords(request: KHReadRequest): List<KHRecord> {
        // Health Connect does not support aggregation yet, so we return raw records
        return try {
            val metric = request.toAggregateMetric()
            val aggregated = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(
                        metric
                    ),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = request.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            .toJavaLocalDateTime(),
                        endTime = request.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            .toJavaLocalDateTime()
                    ),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            return aggregated.mapNotNull { entry -> entry.toKHRecord(request) }
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "aggregatedDailyReadRecords")
            emptyList()
        }
    }

    actual suspend fun readCharacteristic(request: KHCharacteristicType): KHCharacteristicRecord? {
        return null // Health Connect does not support characteristic data
    }


}

internal enum class KHPermissionType { Read, Write }
