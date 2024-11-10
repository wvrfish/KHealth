package com.khealth.sample

import com.khealth.KHDataType
import com.khealth.KHPermission
import com.khealth.KHealth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val permissions = arrayOf(
    KHPermission(dataType = KHDataType.ActiveCaloriesBurned, read = true, write = true),
    KHPermission(dataType = KHDataType.BasalMetabolicRate, read = true, write = true),
    KHPermission(dataType = KHDataType.BloodGlucose, read = true, write = true),
    KHPermission(dataType = KHDataType.BloodPressureSystolic, read = true, write = true),
    KHPermission(dataType = KHDataType.BloodPressureDiastolic, read = true, write = true),
    KHPermission(dataType = KHDataType.BodyFat, read = true, write = true),
    KHPermission(dataType = KHDataType.BodyTemperature, read = true, write = true),
    KHPermission(dataType = KHDataType.BodyWaterMass, read = true, write = true),
    KHPermission(dataType = KHDataType.BoneMass, read = true, write = true),
    KHPermission(dataType = KHDataType.CervicalMucus, read = true, write = true),
    KHPermission(dataType = KHDataType.CyclingPedalingCadence, read = true, write = true),
    KHPermission(dataType = KHDataType.Distance, read = true, write = true),
    KHPermission(dataType = KHDataType.ElevationGained, read = true, write = true),
    KHPermission(dataType = KHDataType.ExerciseSession, read = true, write = true),
    KHPermission(dataType = KHDataType.FloorsClimbed, read = true, write = true),
    KHPermission(dataType = KHDataType.HeartRate, read = true, write = true),
    KHPermission(dataType = KHDataType.HeartRateVariability, read = true, write = true),
    KHPermission(dataType = KHDataType.Height, read = true, write = true),
    KHPermission(dataType = KHDataType.Hydration, read = true, write = true),
    KHPermission(dataType = KHDataType.IntermenstrualBleeding, read = true, write = true),
    KHPermission(dataType = KHDataType.Menstruation, read = true, write = true),
    KHPermission(dataType = KHDataType.LeanBodyMass, read = true, write = true),
    KHPermission(dataType = KHDataType.MenstruationFlow, read = true, write = true),
    KHPermission(dataType = KHDataType.OvulationTest, read = true, write = true),
    KHPermission(dataType = KHDataType.OxygenSaturation, read = true, write = true),
    KHPermission(dataType = KHDataType.Power, read = true, write = true),
    KHPermission(dataType = KHDataType.RespiratoryRate, read = true, write = true),
    KHPermission(dataType = KHDataType.RestingHeartRate, read = true, write = true),
    KHPermission(dataType = KHDataType.SexualActivity, read = true, write = true),
    KHPermission(dataType = KHDataType.SleepSession, read = true, write = true),
    KHPermission(dataType = KHDataType.RunningSpeed, read = true, write = true),
    KHPermission(dataType = KHDataType.CyclingSpeed, read = true, write = true),
    KHPermission(dataType = KHDataType.StepCount, read = true, write = true),
    KHPermission(dataType = KHDataType.Vo2Max, read = true, write = true),
    KHPermission(dataType = KHDataType.Weight, read = true, write = true),
    KHPermission(dataType = KHDataType.WheelChairPushes, read = true, write = true),
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
        try {
            val response = kHealth.requestPermissions(*permissions)
            println("request response is: $response")
        } catch (t: Throwable) {
            println("Error is: $t")
        }
    }
}
