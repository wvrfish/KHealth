package com.khealth

object HealthStoreNotAvailableException : Exception(
    "$HealthStoreName is not available for the current device!"
)

object HealthStoreNotInitialisedException : Exception(
    "KHealth has not been initialised yet! Please make sure to call " +
            "kHealth.initialise() before trying to access any other methods."
)

object WriteActiveCaloriesBurnedException : Exception(
    "Writing ${KHDataType.ActiveCaloriesBurned} to HealthKit store failed! " +
            "Please check if you have the permission to write ${KHDataType.ActiveCaloriesBurned}."
)
