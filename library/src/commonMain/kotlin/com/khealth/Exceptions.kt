package com.khealth

object HealthStoreNotAvailableException : Exception(
    "$HealthStoreName is not available for the current device!"
)

object HealthStoreNotInitialisedException : Exception(
    "KHealth has not been initialised yet! Please make sure to call " +
            "kHealth.initialise() before trying to access any other methods."
)

data class NoWriteAccessException(private val forPermission: String? = null) : Exception(
    "Writing to $HealthStoreName failed! Please make sure you have write permissions for " +
            "${forPermission ?: "all permissions you're trying to ask"}."
)
