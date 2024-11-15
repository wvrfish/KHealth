package com.khealth.sample

import com.khealth.KHCervicalMucusAppearance
import com.khealth.KHCyclingPedalingCadenceSample
import com.khealth.KHDataType
import com.khealth.KHHeartRateSample
import com.khealth.KHMenstruationFlowType
import com.khealth.KHOvulationTestResult
import com.khealth.KHPermission
import com.khealth.KHPermissionStatus
import com.khealth.KHPermissionWithStatus
import com.khealth.KHPowerSample
import com.khealth.KHRecord
import com.khealth.KHSleepStage
import com.khealth.KHSleepStageSample
import com.khealth.KHSpeedSample
import com.khealth.KHUnit
import com.khealth.KHealth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

private val permissions = KHDataType.entries
    .map { type -> KHPermission(type, read = true, write = true) }
    .toTypedArray()

private val coroutineScope = MainScope()

fun sampleCheckAllPerms(kHealth: KHealth) {
    coroutineScope.launch {
        val response = kHealth.checkPermissions(*permissions)
        printResponse(response)
    }
}

fun sampleRequestAllPerms(kHealth: KHealth) {
    coroutineScope.launch {
        try {
            val response = kHealth.requestPermissions(*permissions)
            printResponse(response)
        } catch (t: Throwable) {
            println("Error is: $t")
        }
    }
}

fun sampleWriteData(kHealth: KHealth) {
    coroutineScope.launch {
        val insertResponse = kHealth.writeData(
            KHRecord.ActiveCaloriesBurned(
                energy = KHUnit.Energy.KiloCalorie(3.0),
                startTime = Clock.System.now().minus(10.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.BasalMetabolicRate(
                rateAndroid = KHUnit.Power.Watt(4.0),
                rateApple = KHUnit.Energy.KiloCalorie(4.0),
                time = Clock.System.now(),
            ),
            KHRecord.BloodGlucose(
                level = KHUnit.BloodGlucose.MillimolesPerLiter(2.5),
                time = Clock.System.now(),
            ),
            KHRecord.BloodPressure(
                systolic = KHUnit.Pressure.MillimeterOfMercury(121.0),
                diastolic = KHUnit.Pressure.MillimeterOfMercury(79.0),
                time = Clock.System.now(),
            ),
            KHRecord.BodyFat(
                percentage = 15.0,
                time = Clock.System.now(),
            ),
            KHRecord.BodyTemperature(
                temperature = KHUnit.Temperature.Fahrenheit(98.3),
                time = Clock.System.now(),
            ),
            KHRecord.BodyWaterMass(
                mass = KHUnit.Mass.Gram(192.5),
                time = Clock.System.now(),
            ),
            KHRecord.BoneMass(
                mass = KHUnit.Mass.Gram(93.77),
                time = Clock.System.now(),
            ),
            KHRecord.CervicalMucus(
                appearance = KHCervicalMucusAppearance.EggWhite,
                time = Clock.System.now(),
            ),
            KHRecord.CyclingPedalingCadence(
                samples = listOf(
                    KHCyclingPedalingCadenceSample(
                        revolutionsPerMinute = 48.0,
                        time = Clock.System.now() - 9.minutes,
                    ),
                    KHCyclingPedalingCadenceSample(
                        revolutionsPerMinute = 56.0,
                        time = Clock.System.now(),
                    ),
                ),
                startTime = Clock.System.now() - 10.minutes,
                endTime = Clock.System.now(),
            ),
            KHRecord.Distance(
                distance = KHUnit.Length.Meter(4.9),
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.ElevationGained(
                elevation = KHUnit.Length.Meter(2.2),
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.FloorsClimbed(
                floors = 5.0,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.HeartRate(
                samples = listOf(
                    KHHeartRateSample(beatsPerMinute = 135, time = Clock.System.now())
                ),
            ),
            KHRecord.HeartRateVariability(
                heartRateVariabilityMillis = 51.9,
                time = Clock.System.now(),
            ),
            KHRecord.Height(
                height = KHUnit.Length.Meter(1.78),
                time = Clock.System.now(),
            ),
            KHRecord.Hydration(
                volume = KHUnit.Volume.Liter(3.3),
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.IntermenstrualBleeding(
                time = Clock.System.now(),
            ),
            KHRecord.LeanBodyMass(
                mass = KHUnit.Mass.Gram(1120.0),
                time = Clock.System.now(),
            ),
            KHRecord.MenstruationPeriod(
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.MenstruationFlow(
                flowType = KHMenstruationFlowType.Medium,
                time = Clock.System.now(),
                isStartOfCycle = false,
            ),
            KHRecord.OvulationTest(
                result = KHOvulationTestResult.Positive,
                time = Clock.System.now(),
            ),
            KHRecord.OxygenSaturation(
                percentage = 66.37,
                time = Clock.System.now(),
            ),
            KHRecord.Power(
                samples = listOf(
                    KHPowerSample(
                        power = KHUnit.Power.Watt(50.0),
                        time = Clock.System.now(),
                    )
                )
            ),
            KHRecord.RespiratoryRate(
                rate = 131.0,
                time = Clock.System.now(),
            ),
            KHRecord.RestingHeartRate(
                beatsPerMinute = 112,
                time = Clock.System.now(),
            ),
            KHRecord.SexualActivity(
                didUseProtection = true,
                time = Clock.System.now(),
            ),
            KHRecord.SleepSession(
                samples = listOf(
                    KHSleepStageSample(
                        stage = KHSleepStage.Sleeping,
                        startTime = Clock.System.now() - 16.minutes,
                        endTime = Clock.System.now() - 15.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.REM,
                        startTime = Clock.System.now() - 14.minutes,
                        endTime = Clock.System.now() - 13.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Deep,
                        startTime = Clock.System.now() - 12.minutes,
                        endTime = Clock.System.now() - 11.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Awake,
                        startTime = Clock.System.now() - 10.minutes,
                        endTime = Clock.System.now() - 9.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Light,
                        startTime = Clock.System.now() - 8.minutes,
                        endTime = Clock.System.now() - 7.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.AwakeInBed,
                        startTime = Clock.System.now() - 6.minutes,
                        endTime = Clock.System.now() - 5.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.AwakeOutOfBed,
                        startTime = Clock.System.now() - 4.minutes,
                        endTime = Clock.System.now() - 3.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Unknown,
                        startTime = Clock.System.now() - 2.minutes,
                        endTime = Clock.System.now() - 1.minutes,
                    )
                ),
            ),
            KHRecord.RunningSpeed(
                samples = listOf(
                    KHSpeedSample(
                        speed = KHUnit.Velocity.KilometersPerHour(30.0),
                        time = Clock.System.now(),
                    )
                ),
            ),
            KHRecord.CyclingSpeed(
                samples = listOf(
                    KHSpeedSample(
                        speed = KHUnit.Velocity.KilometersPerHour(10.0),
                        time = Clock.System.now(),
                    )
                ),
            ),
            KHRecord.StepCount(
                count = 24,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.Vo2Max(
                vo2MillilitersPerMinuteKilogram = 55.3,
                time = Clock.System.now(),
            ),
            KHRecord.Weight(
                weight = KHUnit.Mass.Pound(180.33),
                time = Clock.System.now(),
            ),
            KHRecord.WheelChairPushes(
                count = 50,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
        )
        println("Data insert response: $insertResponse")
    }
}

private fun printResponse(response: Set<KHPermissionWithStatus>) {
    println(
        "Request Response: ${
            response.joinToString {
                "${it.permission.dataType} -> " +
                        (if (it.readStatus == KHPermissionStatus.Granted) "R" else "") +
                        if (it.writeStatus == KHPermissionStatus.Granted) "+W" else ""
            }
        }"
    )
}
