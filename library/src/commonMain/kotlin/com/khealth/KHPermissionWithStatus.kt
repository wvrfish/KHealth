package com.khealth

data class KHPermissionWithStatus(
    val permission: KHPermission,
    val readStatus: KHPermissionStatus,
    val writeStatus: KHPermissionStatus,
)
