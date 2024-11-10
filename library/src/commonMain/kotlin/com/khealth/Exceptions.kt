package com.khealth

object HealthStoreNotAvailableException : Exception(
    "$HealthStoreName is not available for the current device!"
)

object HealthStoreNotInitialisedException : Exception(
    "KHealth has not been initialised yet! Please make sure to call " +
            "kHealth.initialise() before trying to access any other methods."
)

object WriteActiveCaloriesBurnedException : Exception(
    "Writing ActiveEnergyBurned to HealthKit store failed! " +
            "Check if you have the permission to write ActiveEnergyBurned!"
)
