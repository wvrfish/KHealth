package com.khealth

expect class KHealth {
    fun initialise()

    val isHealthStoreAvailable: Boolean

    internal fun verifyHealthStoreAvailability()

    suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermissionWithStatus>

    suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermissionWithStatus>

    suspend fun writeActiveCaloriesBurned(vararg records: KHRecord<KHUnit.Energy>): KHWriteResponse
}
