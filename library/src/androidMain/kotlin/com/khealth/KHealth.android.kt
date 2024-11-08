package com.khealth

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord

actual class KHealth(private val context: Context) {
    private val client get() = HealthConnectClient.getOrCreate(context)

    actual val isHealthStoreAvailable: Boolean
        get() = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    actual suspend fun hasPermission(entry: PermissionEntry): PermissionStatus {
        try {
            if (!isHealthStoreAvailable) return PermissionStatus.Denied
            val permissions: Set<String> = buildSet {
                val record = when (entry) {
                    is PermissionEntry.HeartRate -> HeartRateRecord::class
                }
                if (entry.isReadGranted) add(HealthPermission.getReadPermission(record))
                if (entry.isWriteGranted) add(HealthPermission.getWritePermission(record))
            }

            val granted = client.permissionController
                .getGrantedPermissions()
                .containsAll(permissions)

            return if (granted) PermissionStatus.Granted else PermissionStatus.Denied
        } catch (t: Throwable) {
            logError(t)
            return PermissionStatus.NotDetermined
        }
    }
}
