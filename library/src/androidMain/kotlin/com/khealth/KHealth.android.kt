package com.khealth

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.BodyWaterMassRecord
import androidx.health.connect.client.records.BoneMassRecord
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.IntermenstrualBleedingRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord
import androidx.health.connect.client.records.OvulationTestRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SexualActivityRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.WheelchairPushesRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.joules
import androidx.health.connect.client.units.kilojoules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaInstant
import kotlin.reflect.KClass

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
    }

    private var activity: ComponentActivity? = null

    private lateinit var client: HealthConnectClient
    private val coroutineScope: CoroutineScope
    private var testIsHealthStoreAvailable: Boolean? = null
    private val permissionsChannel: Channel<Set<String>>

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

    internal actual fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    actual suspend fun checkPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        verifyHealthStoreAvailability()
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        verifyHealthStoreAvailability()
        val permissionSets = permissions.map { entry -> entry.toPermissions() }

        if (::permissionsLauncher.isInitialized) {
            permissionsLauncher.launch(permissionSets.flatten().map { it.first }.toSet())
        } else {
            logError(HealthStoreNotInitialisedException)
        }

        val grantedPermissions = permissionsChannel.receive()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun writeActiveCaloriesBurned(
        vararg records: KHRecord<KHUnit.Energy>
    ): KHWriteResponse {
        try {
            verifyHealthStoreAvailability()
            val hcRecords = records.map { record ->
                ActiveCaloriesBurnedRecord(
                    energy = record.unitValue.toNativeEnergy(),
                    startTime = record.startDateTime.toJavaInstant(),
                    endTime = record.endDateTime.toJavaInstant(),
                    startZoneOffset = null,
                    endZoneOffset = null,
                )
            }
            logDebug("Inserting ${hcRecords.size} ${KHDataType.ActiveCaloriesBurned} records...")
            val responseIDs = client.insertRecords(hcRecords).recordIdsList
            logDebug("Inserted ${responseIDs.size} ${KHDataType.ActiveCaloriesBurned} records")
            return when {
                responseIDs.size != hcRecords.size && responseIDs.isEmpty() -> {
                    KHWriteResponse.Failed(Exception("No records were written!"))
                }

                responseIDs.size != hcRecords.size -> KHWriteResponse.SomeFailed

                else -> KHWriteResponse.Success
            }
        } catch (e: Exception) {
            val parsedException = when (e) {
                is SecurityException -> WriteActiveCaloriesBurnedException
                else -> e
            }
            logError(parsedException)
            return KHWriteResponse.Failed(parsedException)
        }
    }
}

private fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> {
    val entry = this@toPermissions
    return buildSet {
        entry.dataType.toRecordClass()?.let { safeRecord ->
            if (entry.read) {
                add(HealthPermission.getReadPermission(safeRecord) to KHPermissionType.Read)
            }
            if (entry.write) {
                add(HealthPermission.getWritePermission(safeRecord) to KHPermissionType.Write)
            }
        }
    }
}

private fun Array<out KHPermission>.toPermissionsWithStatuses(
    grantedPermissions: Set<String>
): List<KHPermissionWithStatus> = this.mapIndexed { index, entry ->
    val permissionSet = entry.toPermissions()

    val readGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Read }
        ?.first
        ?.let(grantedPermissions::contains)

    val readStatus = readGranted
        ?.let { granted ->
            if (granted) KHPermissionStatus.Granted else KHPermissionStatus.Denied
        }
        ?: KHPermissionStatus.NotDetermined

    val writeGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Write }
        ?.first
        ?.let(grantedPermissions::contains)

    val writeStatus = writeGranted
        ?.let { granted ->
            if (granted) KHPermissionStatus.Granted else KHPermissionStatus.Denied
        }
        ?: KHPermissionStatus.NotDetermined

    KHPermissionWithStatus(
        permission = this[index],
        readStatus = readStatus,
        writeStatus = writeStatus,
    )
}

// TODO: Remove this when nullable code (which is iOS-specific) has been written
@Suppress("RedundantNullableReturnType")
private fun KHDataType.toRecordClass(): KClass<out Record>? = when (this) {
    KHDataType.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord::class
    KHDataType.BasalMetabolicRate -> BasalMetabolicRateRecord::class
    KHDataType.BloodGlucose -> BloodGlucoseRecord::class

    KHDataType.BloodPressureSystolic,
    KHDataType.BloodPressureDiastolic -> BloodPressureRecord::class

    KHDataType.BodyFat -> BodyFatRecord::class
    KHDataType.BodyTemperature -> BodyTemperatureRecord::class
    KHDataType.BodyWaterMass -> BodyWaterMassRecord::class
    KHDataType.BoneMass -> BoneMassRecord::class
    KHDataType.CervicalMucus -> CervicalMucusRecord::class
    KHDataType.CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class
    KHDataType.Distance -> DistanceRecord::class
    KHDataType.ElevationGained -> ElevationGainedRecord::class
    KHDataType.ExerciseSession -> ExerciseSessionRecord::class
    KHDataType.FloorsClimbed -> FloorsClimbedRecord::class
    KHDataType.HeartRate -> HeartRateRecord::class
    KHDataType.HeartRateVariability -> HeartRateVariabilityRmssdRecord::class
    KHDataType.Height -> HeightRecord::class
    KHDataType.Hydration -> HeartRateVariabilityRmssdRecord::class
    KHDataType.IntermenstrualBleeding -> IntermenstrualBleedingRecord::class
    KHDataType.Menstruation -> MenstruationPeriodRecord::class
    KHDataType.LeanBodyMass -> LeanBodyMassRecord::class
    KHDataType.MenstruationFlow -> MenstruationFlowRecord::class
    KHDataType.OvulationTest -> OvulationTestRecord::class
    KHDataType.OxygenSaturation -> OxygenSaturationRecord::class
    KHDataType.Power -> PowerRecord::class
    KHDataType.RespiratoryRate -> RespiratoryRateRecord::class
    KHDataType.RestingHeartRate -> RestingHeartRateRecord::class
    KHDataType.SexualActivity -> SexualActivityRecord::class
    KHDataType.SleepSession -> SleepSessionRecord::class

    KHDataType.RunningSpeed,
    KHDataType.CyclingSpeed -> SpeedRecord::class

    KHDataType.StepCount -> StepsRecord::class
    KHDataType.Vo2Max -> Vo2MaxRecord::class
    KHDataType.Weight -> WeightRecord::class
    KHDataType.WheelChairPushes -> WheelchairPushesRecord::class
}

private fun KHUnit.Energy.toNativeEnergy(): Energy = when (this) {
    is KHUnit.Energy.Joule -> this.value.joules
    is KHUnit.Energy.Kilocalorie -> this.value.kilojoules
}

private enum class KHPermissionType { Read, Write }
