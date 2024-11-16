package com.khealth.sample

import com.khealth.KHCervicalMucusAppearance
import com.khealth.KHCyclingPedalingCadenceSample
import com.khealth.KHDataType
import com.khealth.KHEither
import com.khealth.KHHeartRateSample
import com.khealth.KHMenstruationFlowType
import com.khealth.KHOvulationTestResult
import com.khealth.KHPermission
import com.khealth.KHPermissionStatus
import com.khealth.KHPermissionWithStatus
import com.khealth.KHPowerSample
import com.khealth.KHReadRequest
import com.khealth.KHRecord
import com.khealth.KHSleepStage
import com.khealth.KHSleepStageSample
import com.khealth.KHSpeedSample
import com.khealth.KHUnit
import com.khealth.KHealth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
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
        val insertResponse = kHealth.writeRecords(
            KHRecord.ActiveCaloriesBurned(
                unit = KHUnit.Energy.KiloCalorie,
                value = 3.0,
                startTime = Clock.System.now().minus(10.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.BasalMetabolicRate(
                unit = KHEither(left = KHUnit.Power.Watt, right = KHUnit.Energy.KiloCalorie),
                value = 4.0,
                time = Clock.System.now(),
            ),
            KHRecord.BloodGlucose(
                unit = KHUnit.BloodGlucose.MillimolesPerLiter,
                value = 2.5,
                time = Clock.System.now(),
            ),
            KHRecord.BloodPressure(
                unit = KHUnit.Pressure.MillimeterOfMercury,
                systolicValue = 121.0,
                diastolicValue = 79.0,
                time = Clock.System.now(),
            ),
            KHRecord.BodyFat(
                percentage = 15.0,
                time = Clock.System.now(),
            ),
            KHRecord.BodyTemperature(
                unit = KHUnit.Temperature.Fahrenheit,
                value = 98.3,
                time = Clock.System.now(),
            ),
            KHRecord.BodyWaterMass(
                unit = KHUnit.Mass.Gram,
                value = 192.5,
                time = Clock.System.now(),
            ),
            KHRecord.BoneMass(
                unit = KHUnit.Mass.Gram,
                value = 93.77,
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
                unit = KHUnit.Length.Meter,
                value = 4.9,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.ElevationGained(
                unit = KHUnit.Length.Meter,
                value = 2.2,
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
                unit = KHUnit.Length.Meter,
                value = 1.78,
                time = Clock.System.now(),
            ),
            KHRecord.Hydration(
                unit = KHUnit.Volume.Liter,
                value = 3.3,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.IntermenstrualBleeding(
                time = Clock.System.now(),
            ),
            KHRecord.LeanBodyMass(
                unit = KHUnit.Mass.Gram,
                value = 1120.0,
                time = Clock.System.now(),
            ),
            KHRecord.MenstruationPeriod(
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.MenstruationFlow(
                type = KHMenstruationFlowType.Medium,
                time = Clock.System.now(),
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
                        unit = KHUnit.Power.Watt,
                        value = 50.0,
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
                        unit = KHUnit.Velocity.KilometersPerHour,
                        value = 10.0,
                        time = Clock.System.now(),
                    )
                ),
            ),
            KHRecord.CyclingSpeed(
                samples = listOf(
                    KHSpeedSample(
                        unit = KHUnit.Velocity.KilometersPerHour,
                        value = 30.0,
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
                unit = KHUnit.Mass.Pound,
                value = 180.33,
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

fun sampleReadData(kHealth: KHealth) {
    coroutineScope.launch {
        val startTime = Clock.System.now().minus(1.days)
        val endTime = Clock.System.now()
        val allRecords = with(kHealth) {
            readRecords(
                KHReadRequest.ActiveCaloriesBurned(
                    unit = KHUnit.Energy.Calorie,
                    startTime = startTime,
                    endTime = endTime
                )
            ) +
                    readRecords(
                        KHReadRequest.BasalMetabolicRate(
                            unit = KHEither(
                                left = KHUnit.Power.Watt,
                                right = KHUnit.Energy.Calorie
                            ),
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BloodGlucose(
                            unit = KHUnit.BloodGlucose.MilligramsPerDeciliter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BloodPressure(
                            unit = KHUnit.Pressure.MillimeterOfMercury,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BodyFat(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.BodyTemperature(
                            unit = KHUnit.Temperature.Fahrenheit,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BodyWaterMass(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BoneMass(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.CervicalMucus(
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.CyclingPedalingCadence(
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.Distance(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.ElevationGained(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.FloorsClimbed(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.HeartRate(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.HeartRateVariability(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Height(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.Hydration(
                            unit = KHUnit.Volume.Liter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.IntermenstrualBleeding(
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.LeanBodyMass(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.MenstruationPeriod(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.MenstruationFlow(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.OvulationTest(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.OxygenSaturation(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Power(
                            unit = KHUnit.Power.Watt,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.RespiratoryRate(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.RestingHeartRate(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.SexualActivity(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.SleepSession(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.RunningSpeed(
                            unit = KHUnit.Velocity.MetersPerSecond,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.CyclingSpeed(
                            unit = KHUnit.Velocity.MetersPerSecond,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.StepCount(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Vo2Max(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Weight(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.WheelChairPushes(startTime = startTime, endTime = endTime)
                    )
        }
        println("All records: $allRecords")
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
