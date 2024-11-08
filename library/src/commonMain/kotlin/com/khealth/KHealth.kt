package com.khealth

expect class KHealth {
    val isHealthStoreAvailable: Boolean
    suspend fun hasPermission(entry: PermissionEntry): PermissionStatus
}
