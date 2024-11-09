package com.khealth.sample

import com.khealth.KHPermission
import com.khealth.KHealth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val permissions = arrayOf(
    KHPermission.HeartRate(read = true, write = true),
    KHPermission.StepCount(read = true, write = true),
)

private val kHealth = KHealth()

private val coroutineScope = MainScope()

fun checkAllPerms() {
    coroutineScope.launch {
        val response = kHealth.checkPermissions(*permissions)
        println("check response is: $response")
    }
}

fun requestAllPerms() {
    coroutineScope.launch {
        val response = kHealth.requestPermissions(*permissions)
        println("request response is: $response")
    }
}
