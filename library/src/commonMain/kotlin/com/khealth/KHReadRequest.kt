package com.khealth

import kotlinx.datetime.Instant

// TODO: Write complete documentation
sealed class KHReadRequest(
    internal val startDateTime: Instant,
    internal val endDateTime: Instant,
    internal val dataType: KHDataType
) {
    data class ActiveCaloriesBurned(
        val unit: KHUnit.Energy,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.ActiveCaloriesBurned
    )

    data class BasalMetabolicRate(
        val unit: KHEither<KHUnit.Power, KHUnit.Energy>,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BasalMetabolicRate
    )

    data class BloodGlucose(
        val unit: KHUnit.BloodGlucose,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BloodGlucose
    )

    data class BloodPressure(
        val unit: KHUnit.Pressure,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BloodPressure
    )

    data class BodyFat(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BodyFat
    )

    data class BodyTemperature(
        val unit: KHUnit.Temperature,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BodyTemperature
    )

    data class BodyWaterMass(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BodyWaterMass
    )

    data class BoneMass(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.BoneMass
    )

    data class CervicalMucus(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.CervicalMucus
    )

    data class CyclingPedalingCadence(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.CyclingPedalingCadence
    )

    data class Distance(
        val unit: KHUnit.Length,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.Distance
    )

    data class ElevationGained(
        val unit: KHUnit.Length,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.ElevationGained
    )

    data class FloorsClimbed(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.FloorsClimbed
    )

    data class HeartRate(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.HeartRate
    )

    data class HeartRateVariability(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.HeartRateVariability
    )

    data class Height(
        val unit: KHUnit.Length,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.Height
    )

    data class Hydration(
        val unit: KHUnit.Volume,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.Hydration
    )

    data class IntermenstrualBleeding(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.IntermenstrualBleeding
    )

    data class LeanBodyMass(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.LeanBodyMass
    )

    data class MenstruationPeriod(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.MenstruationPeriod
    )

    data class MenstruationFlow(
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.MenstruationFlow
    )

    data class OvulationTest(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.OvulationTest
    )

    data class OxygenSaturation(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.OxygenSaturation
    )

    data class Power(
        val unit: KHUnit.Power,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(startDateTime = startTime, endDateTime = endTime, dataType = KHDataType.Power)

    data class RespiratoryRate(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.RespiratoryRate
    )

    data class RestingHeartRate(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.RestingHeartRate
    )

    data class SexualActivity(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.SexualActivity
    )

    data class SleepSession(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.SleepSession
    )

    data class Speed(
        val unit: KHUnit.Velocity,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(startDateTime = startTime, endDateTime = endTime, dataType = KHDataType.Speed)

    data class RunningSpeed(
        val unit: KHUnit.Velocity,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.RunningSpeed
    )

    data class CyclingSpeed(
        val unit: KHUnit.Velocity,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.CyclingSpeed
    )

    data class StepCount(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.StepCount
    )

    data class Vo2Max(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.Vo2Max
    )

    data class Weight(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.Weight
    )

    data class WheelChairPushes(
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest(
        startDateTime = startTime,
        endDateTime = endTime,
        dataType = KHDataType.WheelChairPushes
    )
}
