package com.khealth

import kotlinx.cinterop.UnsafeNumber
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate

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

    @OptIn(UnsafeNumber::class)
    actual suspend fun hasPermission(entry: PermissionEntry): PermissionStatus {
        return try {
            if (!isHealthStoreAvailable) return PermissionStatus.Denied
            val type = HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)
                ?: throw Exception("Type not found!")
            val status = store.authorizationStatusForType(type)
            when (status) {
                HKAuthorizationStatusSharingAuthorized -> PermissionStatus.Granted
                HKAuthorizationStatusSharingDenied -> PermissionStatus.Denied
                HKAuthorizationStatusNotDetermined -> PermissionStatus.NotDetermined
                else -> PermissionStatus.Denied
            }
        } catch (t: Throwable) {
            logError(t)
            PermissionStatus.NotDetermined
        }
    }
}
