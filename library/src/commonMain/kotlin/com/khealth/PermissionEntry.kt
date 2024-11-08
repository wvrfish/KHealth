package com.khealth

sealed class PermissionEntry(
    internal val isReadGranted: Boolean,
    internal val isWriteGranted: Boolean
) {
    data class HeartRate(val read: Boolean, val write: Boolean) :
        PermissionEntry(isReadGranted = read, isWriteGranted = write)
}
