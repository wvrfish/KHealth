package com.khealth

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toNSDate
import platform.Foundation.NSError
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
import platform.HealthKit.HKCategoryValueCervicalMucusQualityCreamy
import platform.HealthKit.HKCategoryValueCervicalMucusQualityDry
import platform.HealthKit.HKCategoryValueCervicalMucusQualityEggWhite
import platform.HealthKit.HKCategoryValueCervicalMucusQualitySticky
import platform.HealthKit.HKCategoryValueCervicalMucusQualityWatery
import platform.HealthKit.HKCategoryValueMenstrualFlowHeavy
import platform.HealthKit.HKCategoryValueMenstrualFlowLight
import platform.HealthKit.HKCategoryValueMenstrualFlowMedium
import platform.HealthKit.HKCategoryValueMenstrualFlowUnspecified
import platform.HealthKit.HKCategoryValueNotApplicable
import platform.HealthKit.HKCategoryValueOvulationTestResultEstrogenSurge
import platform.HealthKit.HKCategoryValueOvulationTestResultIndeterminate
import platform.HealthKit.HKCategoryValueOvulationTestResultNegative
import platform.HealthKit.HKCategoryValueOvulationTestResultPositive
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepCore
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepDeep
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepREM
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepUnspecified
import platform.HealthKit.HKCategoryValueSleepAnalysisAwake
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetadataKeyMenstrualCycleStart
import platform.HealthKit.HKMetadataKeySexualActivityProtectionUsed
import platform.HealthKit.HKMetricPrefixDeci
import platform.HealthKit.HKMetricPrefixKilo
import platform.HealthKit.HKMetricPrefixMilli
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
import platform.HealthKit.HKSample
import platform.HealthKit.HKUnit
import platform.HealthKit.HKUnitMolarMassBloodGlucose
import platform.HealthKit.countUnit
import platform.HealthKit.dayUnit
import platform.HealthKit.degreeCelsiusUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.fluidOunceUSUnit
import platform.HealthKit.gramUnit
import platform.HealthKit.gramUnitWithMetricPrefix
import platform.HealthKit.hourUnit
import platform.HealthKit.inchUnit
import platform.HealthKit.jouleUnit
import platform.HealthKit.jouleUnitWithMetricPrefix
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.largeCalorieUnit
import platform.HealthKit.literUnit
import platform.HealthKit.literUnitWithMetricPrefix
import platform.HealthKit.meterUnit
import platform.HealthKit.meterUnitWithMetricPrefix
import platform.HealthKit.mileUnit
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.moleUnitWithMetricPrefix
import platform.HealthKit.ounceUnit
import platform.HealthKit.percentUnit
import platform.HealthKit.poundUnit
import platform.HealthKit.secondUnit
import platform.HealthKit.secondUnitWithMetricPrefix
import platform.HealthKit.smallCalorieUnit
import platform.HealthKit.unitDividedByUnit
import platform.HealthKit.wattUnit
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

                    success -> continuation.resume(value = KHWriteResponse.Success)
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
}

private fun getTypes(
    from: Array<out KHPermission>,
    where: (KHPermission) -> Boolean
): Set<HKObjectType> = from.filter(where)
    .mapNotNull { it.dataType.toHKObjectTypesOrNull()?.filterNotNull() }
    .flatten()
    .toSet()

internal fun KHDataType.toHKObjectTypesOrNull(): List<HKObjectType?>? {
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
private fun KHRecord.toHKSamplesOrNull(): List<HKSample>? {
    val objectTypes = dataType.toHKObjectTypesOrNull() ?: return null
    return when (this) {
        is KHRecord.ActiveCaloriesBurned -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = energy.toNativeEnergy(),
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.BasalMetabolicRate -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = rateApple.toNativeEnergy(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        // Ref: https://stackoverflow.com/a/30225338
        is KHRecord.BloodGlucose -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = when (level) {
                    is KHUnit.BloodGlucose.MillimolesPerLiter -> HKQuantity.quantityWithUnit(
                        unit = HKUnit
                            .moleUnitWithMetricPrefix(
                                HKMetricPrefixMilli,
                                HKUnitMolarMassBloodGlucose
                            )
                            .unitDividedByUnit(HKUnit.literUnit()),
                        doubleValue = level.value
                    )

                    is KHUnit.BloodGlucose.MilligramsPerDeciliter -> HKQuantity.quantityWithUnit(
                        unit = HKUnit
                            .gramUnitWithMetricPrefix(HKMetricPrefixMilli)
                            .unitDividedByUnit(HKUnit.literUnitWithMetricPrefix(HKMetricPrefixDeci)),
                        doubleValue = level.value
                    )
                },
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        // Ref: https://stackoverflow.com/a/25848858
        is KHRecord.BloodPressure -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = if (objectType == CommonHKObjectTypes.bloodPressureSystolic) {
                    systolic.toNativePressure()
                } else {
                    diastolic.toNativePressure()
                },
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BodyFat -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = HKUnit.percentUnit(),
                    doubleValue = percentage / 100,
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BodyTemperature -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = when (temperature) {
                    is KHUnit.Temperature.Celsius -> HKQuantity.quantityWithUnit(
                        unit = HKUnit.degreeCelsiusUnit(),
                        doubleValue = temperature.value,
                    )

                    is KHUnit.Temperature.Fahrenheit -> HKQuantity.quantityWithUnit(
                        unit = HKUnit.degreeFahrenheitUnit(),
                        doubleValue = temperature.value,
                    )
                },
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BodyWaterMass -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = mass.toNativeMass(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.BoneMass -> null

        is KHRecord.CervicalMucus -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = when (appearance) {
                    KHCervicalMucusAppearance.Creamy -> HKCategoryValueCervicalMucusQualityCreamy
                    KHCervicalMucusAppearance.Dry -> HKCategoryValueCervicalMucusQualityDry
                    KHCervicalMucusAppearance.EggWhite -> HKCategoryValueCervicalMucusQualityEggWhite
                    KHCervicalMucusAppearance.Sticky -> HKCategoryValueCervicalMucusQualitySticky
                    KHCervicalMucusAppearance.Watery -> HKCategoryValueCervicalMucusQualityWatery
                },
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.CyclingPedalingCadence -> null

        is KHRecord.Distance -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = distance.toNativeLength(),
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }

        is KHRecord.ElevationGained -> null

        is KHRecord.FloorsClimbed -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = HKUnit.countUnit(),
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
                        unit = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit()),
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
                    unit = HKUnit.secondUnitWithMetricPrefix(HKMetricPrefixMilli),
                    doubleValue = heartRateVariabilityMillis,
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Height -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = height.toNativeLength(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Hydration -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = when (volume) {
                    is KHUnit.Volume.FluidOunceUS -> HKQuantity.quantityWithUnit(
                        unit = HKUnit.fluidOunceUSUnit(),
                        doubleValue = volume.value,
                    )

                    is KHUnit.Volume.Liter -> HKQuantity.quantityWithUnit(
                        unit = HKUnit.literUnit(),
                        doubleValue = volume.value,
                    )
                },
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
                quantity = mass.toNativeMass(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.MenstruationPeriod -> null

        is KHRecord.MenstruationFlow -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = when (flowType) {
                    KHMenstruationFlowType.Unknown -> HKCategoryValueMenstrualFlowUnspecified
                    KHMenstruationFlowType.Light -> HKCategoryValueMenstrualFlowLight
                    KHMenstruationFlowType.Medium -> HKCategoryValueMenstrualFlowMedium
                    KHMenstruationFlowType.Heavy -> HKCategoryValueMenstrualFlowHeavy
                },
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
                metadata = mapOf(HKMetadataKeyMenstrualCycleStart to isStartOfCycle)
            )
        }

        is KHRecord.OvulationTest -> objectTypes.map { objectType ->
            HKCategorySample.categorySampleWithType(
                type = objectType as HKCategoryType,
                value = when (result) {
                    KHOvulationTestResult.High -> HKCategoryValueOvulationTestResultEstrogenSurge
                    KHOvulationTestResult.Negative -> HKCategoryValueOvulationTestResultNegative
                    KHOvulationTestResult.Positive -> HKCategoryValueOvulationTestResultPositive
                    KHOvulationTestResult.Inconclusive -> HKCategoryValueOvulationTestResultIndeterminate
                },
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.OxygenSaturation -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = HKUnit.percentUnit(),
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
                    quantity = when (sample.power) {
                        is KHUnit.Power.KilocaloriePerDay -> HKQuantity.quantityWithUnit(
                            unit = HKUnit.kilocalorieUnit().unitDividedByUnit(HKUnit.dayUnit()),
                            doubleValue = sample.power.value,
                        )

                        is KHUnit.Power.Watt -> HKQuantity.quantityWithUnit(
                            unit = HKUnit.wattUnit(),
                            doubleValue = sample.power.value,
                        )
                    },
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.RespiratoryRate -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit()),
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
                    unit = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit()),
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
                    value = when (sample.stage) {
                        KHSleepStage.Awake,
                        KHSleepStage.AwakeInBed,
                        KHSleepStage.AwakeOutOfBed -> HKCategoryValueSleepAnalysisAwake

                        KHSleepStage.Deep -> HKCategoryValueSleepAnalysisAsleepDeep
                        KHSleepStage.Light -> HKCategoryValueSleepAnalysisAsleepCore
                        KHSleepStage.REM -> HKCategoryValueSleepAnalysisAsleepREM

                        KHSleepStage.Sleeping,
                        KHSleepStage.Unknown -> HKCategoryValueSleepAnalysisAsleepUnspecified
                    },
                    startDate = sample.startTime.toNSDate(),
                    endDate = sample.endTime.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.RunningSpeed -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = objectType as HKQuantityType,
                    quantity = sample.speed.toNativeVelocity(),
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.CyclingSpeed -> samples.map { sample ->
            objectTypes.map { objectType ->
                HKQuantitySample.quantitySampleWithType(
                    quantityType = objectType as HKQuantityType,
                    quantity = sample.speed.toNativeVelocity(),
                    startDate = sample.time.toNSDate(),
                    endDate = sample.time.toNSDate(),
                )
            }
        }.flatten()

        is KHRecord.StepCount -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = HKUnit.countUnit(),
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
                    unit = HKUnit.unitFromString("ml/kg*min"),
                    doubleValue = vo2MillilitersPerMinuteKilogram
                ),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.Weight -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = weight.toNativeMass(),
                startDate = time.toNSDate(),
                endDate = time.toNSDate(),
            )
        }

        is KHRecord.WheelChairPushes -> objectTypes.map { objectType ->
            HKQuantitySample.quantitySampleWithType(
                quantityType = objectType as HKQuantityType,
                quantity = HKQuantity.quantityWithUnit(
                    unit = HKUnit.countUnit(),
                    doubleValue = count.toDouble(),
                ),
                startDate = startTime.toNSDate(),
                endDate = endTime.toNSDate(),
            )
        }
    }
}

@OptIn(UnsafeNumber::class)
private fun KHUnit.Energy.toNativeEnergy(): HKQuantity = when (this) {
    is KHUnit.Energy.Calorie -> HKQuantity.quantityWithUnit(
        unit = HKUnit.smallCalorieUnit(),
        doubleValue = value
    )

    is KHUnit.Energy.Joule -> HKQuantity.quantityWithUnit(
        unit = HKUnit.jouleUnit(),
        doubleValue = value
    )

    is KHUnit.Energy.KiloCalorie -> HKQuantity.quantityWithUnit(
        unit = HKUnit.largeCalorieUnit(),
        doubleValue = value
    )

    is KHUnit.Energy.KiloJoule -> HKQuantity.quantityWithUnit(
        unit = HKUnit.jouleUnitWithMetricPrefix(HKMetricPrefixKilo),
        doubleValue = value
    )
}

private fun KHUnit.Length.toNativeLength(): HKQuantity = when (this) {
    is KHUnit.Length.Inch -> HKQuantity.quantityWithUnit(
        unit = HKUnit.inchUnit(),
        doubleValue = value,
    )

    is KHUnit.Length.Meter -> HKQuantity.quantityWithUnit(
        unit = HKUnit.meterUnit(),
        doubleValue = value,
    )

    is KHUnit.Length.Mile -> HKQuantity.quantityWithUnit(
        unit = HKUnit.mileUnit(),
        doubleValue = value,
    )
}

private fun KHUnit.Mass.toNativeMass(): HKQuantity = when (this) {
    is KHUnit.Mass.Gram -> HKQuantity.quantityWithUnit(
        unit = HKUnit.gramUnit(),
        doubleValue = value,
    )

    is KHUnit.Mass.Ounce -> HKQuantity.quantityWithUnit(
        unit = HKUnit.ounceUnit(),
        doubleValue = value,
    )

    is KHUnit.Mass.Pound -> HKQuantity.quantityWithUnit(
        unit = HKUnit.poundUnit(),
        doubleValue = value,
    )
}

@OptIn(UnsafeNumber::class)
private fun KHUnit.Velocity.toNativeVelocity(): HKQuantity = when (this) {
    is KHUnit.Velocity.KilometersPerHour -> HKQuantity.quantityWithUnit(
        unit = HKUnit
            .meterUnitWithMetricPrefix(HKMetricPrefixKilo)
            .unitDividedByUnit(HKUnit.hourUnit()),
        doubleValue = this.value,
    )

    is KHUnit.Velocity.MetersPerSecond -> HKQuantity.quantityWithUnit(
        unit = HKUnit.meterUnit().unitDividedByUnit(HKUnit.secondUnit()),
        doubleValue = this.value,
    )

    is KHUnit.Velocity.MilesPerHour -> HKQuantity.quantityWithUnit(
        unit = HKUnit.mileUnit().unitDividedByUnit(HKUnit.hourUnit()),
        doubleValue = this.value,
    )
}

private fun KHUnit.Pressure.toNativePressure(): HKQuantity = when (this) {
    is KHUnit.Pressure.MillimeterOfMercury -> HKQuantity.quantityWithUnit(
        unit = HKUnit.millimeterOfMercuryUnit(),
        doubleValue = value
    )
}

private object CommonHKObjectTypes {
    val bloodPressureSystolic =
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic)

    val bloodPressureDiastolic =
        HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic)
}

private fun NSError.toException() = Exception(this.localizedDescription)
internal const val HKNotAuthorizedMessage = "Authorization is not determined"
