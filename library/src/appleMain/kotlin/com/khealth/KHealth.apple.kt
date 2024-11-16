package com.khealth

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDate
import platform.Foundation.NSError
import platform.Foundation.NSSortDescriptor
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryType
import platform.HealthKit.HKCategoryTypeIdentifierCervicalMucusQuality
import platform.HealthKit.HKCategoryTypeIdentifierIntermenstrualBleeding
import platform.HealthKit.HKCategoryTypeIdentifierMenstrualFlow
import platform.HealthKit.HKCategoryTypeIdentifierOvulationTestResult
import platform.HealthKit.HKCategoryTypeIdentifierSexualActivity
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKCategoryValueNotApplicable
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetadataKeyMenstrualCycleStart
import platform.HealthKit.HKMetadataKeySexualActivityProtectionUsed
import platform.HealthKit.HKObjectQueryNoLimit
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBasalEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierCyclingPower
import platform.HealthKit.HKQuantityTypeIdentifierCyclingSpeed
import platform.HealthKit.HKQuantityTypeIdentifierDietaryWater
import platform.HealthKit.HKQuantityTypeIdentifierDistanceWalkingRunning
import platform.HealthKit.HKQuantityTypeIdentifierFlightsClimbed
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeartRateVariabilitySDNN
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierLeanBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierOxygenSaturation
import platform.HealthKit.HKQuantityTypeIdentifierPushCount
import platform.HealthKit.HKQuantityTypeIdentifierRespiratoryRate
import platform.HealthKit.HKQuantityTypeIdentifierRestingHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierRunningSpeed
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKQuantityTypeIdentifierVO2Max
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionStrictStartDate
import platform.HealthKit.HKSample
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleSortIdentifierStartDate
import platform.HealthKit.HKSampleType
import platform.HealthKit.HKUnit
import platform.HealthKit.percentUnit
import platform.HealthKit.predicateForSamplesWithStartDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class KHealth {
    constructor() {
        this.store = HKHealthStore()
    }

    internal constructor(store: HKHealthStore, isHealthStoreAvailable: Boolean) {
        this.store = store
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
    }

    private var store: HKHealthStore
    private var testIsHealthStoreAvailable: Boolean? = null

    actual val isHealthStoreAvailable: Boolean
        get() {
            return testIsHealthStoreAvailable ?: try {
                HKHealthStore.isHealthDataAvailable()
            } catch (t: Throwable) {
                false
            }
        }

    internal actual fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    actual fun initialise() = Unit

    @OptIn(UnsafeNumber::class)
    actual suspend fun checkPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        try {
            verifyHealthStoreAvailability()
            val permissionsWithStatuses = permissions.mapNotNull { permission ->
                val types = permission.dataType.toHKObjectTypesOrNull()?.filterNotNull()

                if (types == null) {
                    logDebug("Type for $permission not found!")
                    return@mapNotNull null
                }

                val isWriteGranted = types.map { type ->
                    when (store.authorizationStatusForType(type)) {
                        HKAuthorizationStatusSharingAuthorized -> KHPermissionStatus.Granted
                        HKAuthorizationStatusSharingDenied -> KHPermissionStatus.Denied
                        HKAuthorizationStatusNotDetermined -> KHPermissionStatus.NotDetermined
                        else -> throw Exception("Unknown authorization status!")
                    }
                }.all { status -> status == KHPermissionStatus.Granted }

                KHPermissionWithStatus(
                    permission = permission,
                    // HealthKit does not provide status for READ permissions for privacy concerns
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = when {
                        !permission.write -> KHPermissionStatus.NotDetermined
                        isWriteGranted -> KHPermissionStatus.Granted
                        else -> KHPermissionStatus.Denied
                    },
                )
            }

            return permissionsWithStatuses.toSet()
        } catch (t: Throwable) {
            logError(t)
            return emptySet()
        }
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> = suspendCoroutine { continuation ->
        try {
            verifyHealthStoreAvailability()
            val coroutineScope = CoroutineScope(continuation.context)
            store.requestAuthorizationToShareTypes(
                typesToShare = getTypes(from = permissions, where = { it.write }),
                readTypes = getTypes(from = permissions, where = { it.read }),
                completion = { _, error ->
                    if (error != null) {
                        logError(error.toException())
                        continuation.resumeWithException(Exception(error.localizedDescription))
                    } else {
                        coroutineScope.launch {
                            continuation.resume(checkPermissions(*permissions))
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            logError(t)
            continuation.resume(emptySet())
        }
    }

    actual suspend fun writeData(
        vararg records: KHRecord
    ): KHWriteResponse = suspendCoroutine { continuation ->
        try {
            verifyHealthStoreAvailability()
            val samples = records.mapNotNull { record -> record.toHKSamplesOrNull() }.flatten()
            store.saveObjects(samples) { success, error ->
                when {
                    error != null -> {
                        val exception = error.toException()
                        val parsedException = when {
                            exception.message?.contains(HKNotAuthorizedMessage) == true ->
                                NoWriteAccessException()

                            else -> exception
                        }
                        logError(parsedException)
                        continuation.resume(KHWriteResponse.Failed(parsedException))
                    }

                    success -> continuation.resume(KHWriteResponse.Success)
                    else -> continuation.resume(
                        value = KHWriteResponse.Failed(throwable = NoWriteAccessException())
                    )
                }
            }
        } catch (t: Throwable) {
            logError(t)
            continuation.resume(KHWriteResponse.Failed(t))
        }
    }

    @OptIn(UnsafeNumber::class)
    actual suspend fun readRecords(request: KHReadRequest): List<KHRecord> {
        val objectTypes = request.dataType
            .toHKObjectTypesOrNull()
            ?.filterNotNull()
            ?: return emptyList()

        val predicate = HKQuery.predicateForSamplesWithStartDate(
            request.startDateTime.toNSDate(),
            request.endDateTime.toNSDate(),
            HKQueryOptionStrictStartDate
        )

        val limit = HKObjectQueryNoLimit

        val sortDescriptors = listOf(
            NSSortDescriptor.sortDescriptorWithKey(
                HKSampleSortIdentifierStartDate,
                ascending = true
            )
        )

        suspend inline fun <reified T : HKSample, reified U> buildSampleQuery(
            primaryObjectType: HKObjectType,
            secondaryObjectType: HKObjectType? = null,
            noinline mapSingleResult: ((T) -> U)? = null,
            noinline mapMultipleResults: ((T, T) -> U)? = null,
        ): List<U> = suspendCoroutine { continuation ->
            try {
                val primaryQuery = HKSampleQuery(
                    sampleType = primaryObjectType as HKSampleType,
                    predicate = predicate,
                    limit = limit,
                    sortDescriptors = sortDescriptors,
                ) { _, primarySamples, primaryError ->
                    if (primaryError != null) {
                        continuation.resumeWithException(primaryError.toException())
                    }

                    when {
                        mapMultipleResults != null && secondaryObjectType != null -> {
                            val secondaryQuery = HKSampleQuery(
                                sampleType = secondaryObjectType as HKSampleType,
                                predicate = predicate,
                                limit = limit,
                                sortDescriptors = sortDescriptors,
                            ) { _, secondarySamples, secondaryError ->
                                if (secondaryError != null) {
                                    continuation.resumeWithException(secondaryError.toException())
                                }

                                val records = primarySamples
                                    ?.filterIsInstance<T>()
                                    ?.mapIndexed { index, primarySample ->
                                        mapMultipleResults(
                                            primarySample,
                                            secondarySamples?.get(index) as T
                                        )
                                    }
                                    ?: emptyList()

                                continuation.resume(records)
                            }
                            store.executeQuery(secondaryQuery)
                        }

                        mapSingleResult != null -> {
                            continuation.resume(
                                primarySamples
                                    ?.filterIsInstance<T>()
                                    ?.map(mapSingleResult)
                                    ?: emptyList()
                            )
                        }

                        else -> continuation.resumeWithException(
                            IllegalStateException("No mapper provided!")
                        )
                    }
                }

                store.executeQuery(primaryQuery)
            } catch (t: Throwable) {
                logError(t)
                continuation.resumeWithException(t)
            }
        }

        return when (request) {
            is KHReadRequest.ActiveCaloriesBurned -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.ActiveCaloriesBurned(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.BasalMetabolicRate -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.BasalMetabolicRate(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit.right,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.BloodGlucose -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.BloodGlucose(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.BloodPressure -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes[0],
                secondaryObjectType = objectTypes[1],
                mapMultipleResults = { primarySample, secondarySample ->
                    val primaryValue = primarySample.quantity toDoubleValueFor request.unit
                    val secondaryValue = secondarySample.quantity toDoubleValueFor request.unit

                    val isFirstTypeSystolic =
                        objectTypes[0] == CommonHKObjectTypes.bloodPressureSystolic

                    KHRecord.BloodPressure(
                        unit = request.unit,
                        systolicValue = if (isFirstTypeSystolic) primaryValue else secondaryValue,
                        diastolicValue = if (isFirstTypeSystolic) secondaryValue else primaryValue,
                        time = primarySample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.BodyFat -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.BodyFat(
                        percentage = sample.quantity.doubleValueForUnit(HKUnit.percentUnit()),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.BodyTemperature -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.BodyTemperature(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.BodyWaterMass -> emptyList()

            is KHReadRequest.BoneMass -> emptyList()

            is KHReadRequest.CervicalMucus -> buildSampleQuery<HKCategorySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.CervicalMucus(
                        appearance = sample.value.toKHCervicalMucusAppearance(),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.CyclingPedalingCadence -> emptyList()

            is KHReadRequest.CyclingSpeed -> {
                val dateGroupedSamples = buildSampleQuery<HKQuantitySample, KHSpeedSample>(
                    primaryObjectType = objectTypes.first(),
                    mapSingleResult = { sample ->
                        KHSpeedSample(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }
                ).groupBy { sample ->
                    sample.time.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }.values.toList()

                return dateGroupedSamples.map { samples ->
                    KHRecord.CyclingSpeed(samples = samples)
                }
            }

            is KHReadRequest.Distance -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.Distance(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.ElevationGained -> emptyList()

            is KHReadRequest.FloorsClimbed -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.FloorsClimbed(
                        floors = sample.quantity.doubleValueForUnit(AppleUnits.count),
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.HeartRate -> {
                val dateGroupedSamples = buildSampleQuery<HKQuantitySample, KHHeartRateSample>(
                    primaryObjectType = objectTypes.first(),
                    mapSingleResult = { sample ->
                        KHHeartRateSample(
                            beatsPerMinute = sample.quantity
                                .doubleValueForUnit(AppleUnits.beatsPerMinute)
                                .toLong(),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }
                ).groupBy { sample ->
                    sample.time.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }.values.toList()

                return dateGroupedSamples.map { samples ->
                    KHRecord.HeartRate(samples = samples)
                }
            }

            is KHReadRequest.HeartRateVariability -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.HeartRateVariability(
                        heartRateVariabilityMillis = sample.quantity.doubleValueForUnit(
                            AppleUnits.millisecond
                        ),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.Height -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.Height(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.Hydration -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.Hydration(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.IntermenstrualBleeding -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.IntermenstrualBleeding(
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.LeanBodyMass -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.LeanBodyMass(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.MenstruationFlow -> buildSampleQuery<HKCategorySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.MenstruationFlow(
                        type = sample.value.toKHMenstruationFlowType(),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.MenstruationPeriod -> emptyList()

            is KHReadRequest.OvulationTest -> buildSampleQuery<HKCategorySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.OvulationTest(
                        result = sample.value.toKHOvulationTestResult(),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.OxygenSaturation -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.OxygenSaturation(
                        percentage = sample.quantity.doubleValueForUnit(AppleUnits.percent),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.Power -> {
                val dateGroupedSamples = buildSampleQuery<HKQuantitySample, KHPowerSample>(
                    primaryObjectType = objectTypes.first(),
                    mapSingleResult = { sample ->
                        KHPowerSample(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }
                ).groupBy { sample ->
                    sample.time.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }.values.toList()

                return dateGroupedSamples.map { samples ->
                    KHRecord.Power(samples = samples)
                }
            }

            is KHReadRequest.RespiratoryRate -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.RespiratoryRate(
                        rate = sample.quantity.doubleValueForUnit(AppleUnits.beatsPerMinute),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.RestingHeartRate -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.RestingHeartRate(
                        beatsPerMinute = sample.quantity
                            .doubleValueForUnit(AppleUnits.beatsPerMinute)
                            .toLong(),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.RunningSpeed -> {
                val dateGroupedSamples = buildSampleQuery<HKQuantitySample, KHSpeedSample>(
                    primaryObjectType = objectTypes.first(),
                    mapSingleResult = { sample ->
                        KHSpeedSample(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }
                ).groupBy { sample ->
                    sample.time.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }.values.toList()

                return dateGroupedSamples.map { samples ->
                    KHRecord.RunningSpeed(samples = samples)
                }
            }

            is KHReadRequest.SexualActivity -> buildSampleQuery<HKCategorySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.SexualActivity(
                        didUseProtection = sample.metadata?.getValue(
                            HKMetadataKeySexualActivityProtectionUsed
                        ) == true,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.SleepSession -> {
                val dateGroupedSamples = buildSampleQuery<HKCategorySample, KHSleepStageSample>(
                    primaryObjectType = objectTypes.first(),
                    mapSingleResult = { sample ->
                        KHSleepStageSample(
                            stage = sample.value.toKHSleepStage(),
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }
                ).groupBy { sample ->
                    sample.endTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }.values.toList()

                return dateGroupedSamples.map { samples ->
                    KHRecord.SleepSession(samples = samples)
                }
            }

            is KHReadRequest.Speed -> emptyList()

            is KHReadRequest.StepCount -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.StepCount(
                        count = sample.quantity.doubleValueForUnit(AppleUnits.count).toLong(),
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.Vo2Max -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.Vo2Max(
                        vo2MillilitersPerMinuteKilogram = sample.quantity.doubleValueForUnit(
                            AppleUnits.vo2Max
                        ),
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.Weight -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.Weight(
                        unit = request.unit,
                        value = sample.quantity toDoubleValueFor request.unit,
                        time = sample.endDate.toKotlinInstant(),
                    )
                }
            )

            is KHReadRequest.WheelChairPushes -> buildSampleQuery<HKQuantitySample, KHRecord>(
                primaryObjectType = objectTypes.first(),
                mapSingleResult = { sample ->
                    KHRecord.WheelChairPushes(
                        count = sample.quantity.doubleValueForUnit(AppleUnits.count).toLong(),
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                    )
                }
            )
        }
    }
}

internal fun getTypes(
    from: Array<out KHPermission>,
    where: (KHPermission) -> Boolean
): Set<HKObjectType> = from.filter(where)
    .mapNotNull { it.dataType.toHKObjectTypesOrNull()?.filterNotNull() }
    .flatten()
    .toSet()

internal fun KHDataType.toHKObjectTypesOrNull(): List<HKObjectType?>? {
    // TODO: Fix sequence of items
    return when (this) {
        KHDataType.ActiveCaloriesBurned -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned)
        )

        KHDataType.BasalMetabolicRate -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBasalEnergyBurned)
        )

        KHDataType.BloodGlucose -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose)
        )

        KHDataType.BloodPressure -> listOf(
            CommonHKObjectTypes.bloodPressureSystolic,
            CommonHKObjectTypes.bloodPressureDiastolic,
        )

        KHDataType.BodyFat -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage)
        )

        KHDataType.BodyTemperature -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature)
        )

        KHDataType.BodyWaterMass -> null

        KHDataType.BoneMass -> null

        KHDataType.CervicalMucus -> listOf(
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierCervicalMucusQuality)
        )

        KHDataType.CyclingPedalingCadence -> null

        KHDataType.Distance -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDistanceWalkingRunning)
        )

        KHDataType.ElevationGained -> null

        KHDataType.FloorsClimbed -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierFlightsClimbed)
        )

        KHDataType.HeartRate -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)
        )

        KHDataType.HeartRateVariability -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRateVariabilitySDNN)
        )

        KHDataType.Height -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight)
        )

        KHDataType.Hydration -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)
        )

        KHDataType.IntermenstrualBleeding -> listOf(
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierIntermenstrualBleeding)
        )

        KHDataType.LeanBodyMass -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierLeanBodyMass)
        )

        KHDataType.MenstruationPeriod -> null

        KHDataType.MenstruationFlow -> listOf(
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierMenstrualFlow)
        )

        KHDataType.OvulationTest -> listOf(
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierOvulationTestResult)
        )

        KHDataType.OxygenSaturation -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierOxygenSaturation)
        )

        KHDataType.Power -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingPower)
        )

        KHDataType.RespiratoryRate -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRespiratoryRate)
        )

        KHDataType.RestingHeartRate -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRestingHeartRate)
        )

        KHDataType.SexualActivity -> listOf(
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSexualActivity)
        )

        KHDataType.SleepSession -> listOf(
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)
        )

        KHDataType.Speed -> null

        KHDataType.RunningSpeed -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRunningSpeed)
        )

        KHDataType.CyclingSpeed -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingSpeed)
        )

        KHDataType.StepCount -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)
        )

        KHDataType.Vo2Max -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierVO2Max)
        )

        KHDataType.Weight -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)
        )

        KHDataType.WheelChairPushes -> listOf(
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierPushCount)
        )
    }
}

// Returning a List because we want to club multiple records in some cases (like BloodPressure
// where we want the user to provide both systolic and diastolic values at once and bifurcate them
// here into 2 separate records).
@OptIn(UnsafeNumber::class)
internal fun KHRecord.toHKSamplesOrNull(): List<HKSample>? {
    val objectTypes = dataType.toHKObjectTypesOrNull() ?: return null
    return when (this) {
        is KHRecord.ActiveCaloriesBurned -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeEnergyFor value,
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.BasalMetabolicRate -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit.right toNativeEnergyFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        // Ref: https://stackoverflow.com/a/30225338
        is KHRecord.BloodGlucose -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeBloodGlucoseFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        // Ref: https://stackoverflow.com/a/25848858
        is KHRecord.BloodPressure -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativePressureFor (
                        if (objectType == CommonHKObjectTypes.bloodPressureSystolic) systolicValue
                        else diastolicValue
                        ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BodyFat -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.percent,
                    doubleValue = percentage / 100,
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BodyTemperature -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeTemperatureFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BodyWaterMass -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeMassFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BoneMass -> null

        is KHRecord.CervicalMucus -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = appearance.toNativeCervicalMucusQuality(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.CyclingPedalingCadence -> null

        is KHRecord.Distance -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeLengthFor value,
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.ElevationGained -> null

        is KHRecord.FloorsClimbed -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.count,
                    doubleValue = floors,
                ),
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.HeartRate -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = objectType as HKQuantityType,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = AppleUnits.beatsPerMinute,
                        doubleValue = sample.beatsPerMinute.toDouble(),
                    ),
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.HeartRateVariability -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.millisecond,
                    doubleValue = heartRateVariabilityMillis,
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Height -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeLengthFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Hydration -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeVolumeFor value,
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.IntermenstrualBleeding -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = HKCategoryValueNotApplicable,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.LeanBodyMass -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeMassFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.MenstruationPeriod -> null

        is KHRecord.MenstruationFlow -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = type.toNativeMenstrualFlow(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
                metadata = mapOf(HKMetadataKeyMenstrualCycleStart to isStartOfCycle)
            )
        }

        is KHRecord.OvulationTest -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = result.toNativeOvulationResult(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.OxygenSaturation -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.percent,
                    doubleValue = percentage / 100,
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Power -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = objectType as HKQuantityType,
                    quantity = sample.unit toNativePowerFor sample.value,
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.RespiratoryRate -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.beatsPerMinute,
                    doubleValue = rate,
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.RestingHeartRate -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.beatsPerMinute,
                    doubleValue = beatsPerMinute.toDouble(),
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.SexualActivity -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = HKCategoryValueNotApplicable,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
                metadata = mapOf(HKMetadataKeySexualActivityProtectionUsed to didUseProtection)
            )
        }

        is KHRecord.SleepSession -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKCategorySample.categorySampleWithType(
                    type = objectType as HKCategoryType,
                    value = sample.stage.toNativeSleepStage(),
                    startDate = sample.startTime.toNSDate(),
                    endDate = sample.endTime.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.Speed -> null

        is KHRecord.RunningSpeed -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = objectType as HKQuantityType,
                    quantity = sample.unit toNativeVelocityFor sample.value,
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.CyclingSpeed -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = objectType as HKQuantityType,
                    quantity = sample.unit toNativeVelocityFor sample.value,
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.StepCount -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.count,
                    doubleValue = count.toDouble()
                ),
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.Vo2Max -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.vo2Max,
                    doubleValue = vo2MillilitersPerMinuteKilogram
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Weight -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = unit toNativeMassFor value,
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.WheelChairPushes -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = AppleUnits.count,
                    doubleValue = count.toDouble(),
                ),
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }
    }
}

internal object CommonHKObjectTypes {
    val bloodPressureSystolic =
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic)

    val bloodPressureDiastolic =
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic)
}

internal fun NSError.toException() = Exception(this.localizedDescription)
internal const val HKNotAuthorizedMessage = "Authorization is not determined"
