package com.khealth

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKCategoryTypeIdentifierCervicalMucusQuality
import platform.HealthKit.HKCategoryTypeIdentifierIntermenstrualBleeding
import platform.HealthKit.HKCategoryTypeIdentifierMenstrualFlow
import platform.HealthKit.HKCategoryTypeIdentifierOvulationTestResult
import platform.HealthKit.HKCategoryTypeIdentifierSexualActivity
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class KHealth {
    private val store = HKHealthStore()

    actual val isHealthStoreAvailable: Boolean
        get() {
            return try {
                HKHealthStore.isHealthDataAvailable()
            } catch (t: Throwable) {
                false
            }
        }

    actual fun initialise() = Unit

    @OptIn(UnsafeNumber::class)
    actual suspend fun checkPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        try {
            val permissionsWithStatuses = permissions.mapNotNull { permission ->
                val type = permission.toHKObjectType()

                if (type == null) {
                    println("Type for $permission not found!")
                    return@mapNotNull null
                }

                KHPermissionWithStatus(
                    permission = permission,
                    // HealthKit does not provide status for READ permissions for privacy concerns
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = when (store.authorizationStatusForType(type)) {
                        HKAuthorizationStatusSharingAuthorized -> KHPermissionStatus.Granted
                        HKAuthorizationStatusSharingDenied -> KHPermissionStatus.Denied
                        HKAuthorizationStatusNotDetermined -> KHPermissionStatus.NotDetermined
                        else -> throw Exception("Unknown authorization status!")
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
            val coroutineScope = CoroutineScope(continuation.context)
            store.requestAuthorizationToShareTypes(
                typesToShare = getTypes(permissions) { it.writeRequested },
                readTypes = getTypes(permissions) { it.readRequested },
                completion = { _, error ->
                    if (error != null) {
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
}

private fun getTypes(permissions: Array<out KHPermission>, predicate: (KHPermission) -> Boolean) =
    permissions.filter(predicate).mapNotNull { it.toHKObjectType() }.toSet()

private fun KHPermission.toHKObjectType(): HKObjectType? {
    return when (this) {
        is KHPermission.ActiveCaloriesBurned ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned)

        is KHPermission.BasalMetabolicRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBasalEnergyBurned)

        is KHPermission.BloodGlucose ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose)

        is KHPermission.BloodPressureSystolic ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic)

        is KHPermission.BloodPressureDiastolic ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic)

        is KHPermission.BodyFat ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage)

        is KHPermission.BodyTemperature ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature)

        is KHPermission.BodyWaterMass ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)

        is KHPermission.BoneMass -> null

        is KHPermission.CervicalMucus ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierCervicalMucusQuality)

        is KHPermission.CyclingPedalingCadence -> null

        is KHPermission.Distance ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDistanceWalkingRunning)

        is KHPermission.ElevationGained -> null

        // TODO: Verify this
        is KHPermission.ExerciseSession -> HKObjectType.workoutType()

        is KHPermission.FloorsClimbed ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierFlightsClimbed)

        is KHPermission.HeartRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)

        is KHPermission.HeartRateVariability ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRateVariabilitySDNN)

        is KHPermission.Height ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight)

        is KHPermission.Hydration ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)

        is KHPermission.IntermenstrualBleeding ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierIntermenstrualBleeding)

        is KHPermission.Menstruation -> null

        is KHPermission.LeanBodyMass ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierLeanBodyMass)

        is KHPermission.MenstruationFlow ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierMenstrualFlow)

        is KHPermission.OvulationTest ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierOvulationTestResult)

        is KHPermission.OxygenSaturation ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierOxygenSaturation)

        is KHPermission.Power ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingPower)

        is KHPermission.RespiratoryRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRespiratoryRate)

        is KHPermission.RestingHeartRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRestingHeartRate)

        is KHPermission.SexualActivity ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSexualActivity)

        is KHPermission.SleepSession ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)

        is KHPermission.RunningSpeed ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRunningSpeed)

        is KHPermission.CyclingSpeed ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingSpeed)

        is KHPermission.StepCount ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)

        is KHPermission.Vo2Max ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierVO2Max)

        is KHPermission.Weight ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)

        is KHPermission.WheelChairPushes ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierPushCount)
    }
}
