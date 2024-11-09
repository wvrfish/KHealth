package com.khealth.sample

import com.khealth.KHPermission
import com.khealth.KHealth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val permissions = arrayOf(
    KHPermission.ActiveCaloriesBurned(read = true, write = true),
    KHPermission.BasalMetabolicRate(read = true, write = true),
    KHPermission.BloodGlucose(read = true, write = true),
    KHPermission.BloodPressureSystolic(read = true, write = true),
    KHPermission.BloodPressureDiastolic(read = true, write = true),
    KHPermission.BodyFat(read = true, write = true),
    KHPermission.BodyTemperature(read = true, write = true),
    KHPermission.BodyWaterMass(read = true, write = true),
    KHPermission.BoneMass(read = true, write = true),
    KHPermission.CervicalMucus(read = true, write = true),
    KHPermission.CyclingPedalingCadence(read = true, write = true),
    KHPermission.Distance(read = true, write = true),
    KHPermission.ElevationGained(read = true, write = true),
    KHPermission.ExerciseSession(read = true, write = true),
    KHPermission.FloorsClimbed(read = true, write = true),
    KHPermission.HeartRate(read = true, write = true),
    KHPermission.HeartRateVariability(read = true, write = true),
    KHPermission.Height(read = true, write = true),
    KHPermission.Hydration(read = true, write = true),
    KHPermission.IntermenstrualBleeding(read = true, write = true),
    KHPermission.Menstruation(read = true, write = true),
    KHPermission.LeanBodyMass(read = true, write = true),
    KHPermission.MenstruationFlow(read = true, write = true),
    KHPermission.OvulationTest(read = true, write = true),
    KHPermission.OxygenSaturation(read = true, write = true),
    KHPermission.Power(read = true, write = true),
    KHPermission.RespiratoryRate(read = true, write = true),
    KHPermission.RestingHeartRate(read = true, write = true),
    KHPermission.SexualActivity(read = true, write = true),
    KHPermission.SleepSession(read = true, write = true),
    KHPermission.RunningSpeed(read = true, write = true),
    KHPermission.CyclingSpeed(read = true, write = true),
    KHPermission.StepCount(read = true, write = true),
    KHPermission.Vo2Max(read = true, write = true),
    KHPermission.Weight(read = true, write = true),
    KHPermission.WheelChairPushes(read = true, write = true),
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
