package com.khealth

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
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
            val permissionsWithStatuses = permissions.map { permission ->
                val type = permission.toHKObjectType() ?: throw Exception("Type not found!")
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
                typesToShare = permissions
                    .filter { permission -> permission.writeRequested }
                    .map { it.toHKObjectType() }
                    .toSet(),
                readTypes = permissions
                    .filter { permission -> permission.readRequested }
                    .map { it.toHKObjectType() }
                    .toSet(),
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
            continuation.resumeWithException(t)
        }
    }

    private fun KHPermission.toHKObjectType(): HKObjectType? {
        return when (this) {
            is KHPermission.HeartRate -> HKObjectType.quantityTypeForIdentifier(
                HKQuantityTypeIdentifierHeartRate
            )

            is KHPermission.StepCount -> HKObjectType.quantityTypeForIdentifier(
                HKQuantityTypeIdentifierStepCount
            )
        }
    }
}
