package com.khealth

sealed class KHPermission(
    internal val readRequested: Boolean,
    internal val writeRequested: Boolean
) {
    data class HeartRate(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    data class StepCount(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)
}
