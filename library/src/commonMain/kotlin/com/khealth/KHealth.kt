package com.khealth

expect class KHealth {
    fun initialise()
    val isHealthStoreAvailable: Boolean
    suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermissionWithStatus>
    suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermissionWithStatus>
}
