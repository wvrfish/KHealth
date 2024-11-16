package com.khealth

import kotlinx.datetime.Instant

// TODO: Write complete documentation
// TODO: Add emptyList check for record types with `samples`
sealed class KHRecord(internal val dataType: KHDataType) {
    data class ActiveCaloriesBurned(
        val unit: KHUnit.Energy,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.ActiveCaloriesBurned)

    /**
     * Android and Apple use different units for this record
     */
    data class BasalMetabolicRate(
        val unit: KHEither<KHUnit.Power, KHUnit.Energy>,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BasalMetabolicRate)

    data class BloodGlucose(
        val unit: KHUnit.BloodGlucose,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BloodGlucose)

    data class BloodPressure(
        val unit: KHUnit.Pressure,
        val systolicValue: Double,
        val diastolicValue: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BloodPressure)

    data class BodyFat(
        val percentage: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BodyFat)

    data class BodyTemperature(
        val unit: KHUnit.Temperature,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BodyTemperature)

    /**
     * Available on Android only.
     */
    data class BodyWaterMass(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BodyWaterMass)

    /**
     * Available on Android only.
     */
    data class BoneMass(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.BoneMass)

    data class CervicalMucus(
        val appearance: KHCervicalMucusAppearance,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.CervicalMucus)

    /**
     * Available on Android only.
     */
    data class CyclingPedalingCadence(
        val samples: List<KHCyclingPedalingCadenceSample>,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.CyclingPedalingCadence)

    data class Distance(
        val unit: KHUnit.Length,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.Distance)

    /**
     * Available on Android only.
     */
    data class ElevationGained(
        val unit: KHUnit.Length,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.ElevationGained)

    data class FloorsClimbed(
        val floors: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.FloorsClimbed)

    data class HeartRate(
        val samples: List<KHHeartRateSample>
    ) : KHRecord(dataType = KHDataType.HeartRate)

    data class HeartRateVariability(
        val heartRateVariabilityMillis: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.HeartRateVariability)

    data class Height(
        val unit: KHUnit.Length,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.Height)

    data class Hydration(
        val unit: KHUnit.Volume,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.Hydration)

    data class IntermenstrualBleeding(
        val time: Instant
    ) : KHRecord(dataType = KHDataType.IntermenstrualBleeding)

    data class LeanBodyMass(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant
    ) : KHRecord(dataType = KHDataType.LeanBodyMass)

    /**
     * Available on Android only.
     */
    data class MenstruationPeriod(
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.MenstruationPeriod)

    data class MenstruationFlow(
        val type: KHMenstruationFlowType,
        val time: Instant,
        val isStartOfCycle: Boolean = false,
    ) : KHRecord(dataType = KHDataType.MenstruationFlow)

    data class OvulationTest(
        val result: KHOvulationTestResult,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.OvulationTest)

    data class OxygenSaturation(
        val percentage: Double,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.OxygenSaturation)

    data class Power(
        val samples: List<KHPowerSample>
    ) : KHRecord(dataType = KHDataType.Power)

    data class RespiratoryRate(
        val rate: Double,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.RespiratoryRate)

    data class RestingHeartRate(
        val beatsPerMinute: Long,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.RestingHeartRate)

    data class SexualActivity(
        val didUseProtection: Boolean,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.SexualActivity)

    data class SleepSession(
        val samples: List<KHSleepStageSample>
    ) : KHRecord(dataType = KHDataType.SleepSession)

    /**
     * Available on Android only.
     */
    data class Speed(
        val samples: List<KHSpeedSample>,
    ) : KHRecord(dataType = KHDataType.Speed)

    /**
     * Available on Apple only.
     */
    data class RunningSpeed(
        val samples: List<KHSpeedSample>,
    ) : KHRecord(dataType = KHDataType.RunningSpeed)

    /**
     * Available on Apple only.
     */
    data class CyclingSpeed(
        val samples: List<KHSpeedSample>,
    ) : KHRecord(dataType = KHDataType.CyclingSpeed)

    data class StepCount(
        val count: Long,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord(dataType = KHDataType.StepCount)

    data class Vo2Max(
        val vo2MillilitersPerMinuteKilogram: Double,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.Vo2Max)

    data class Weight(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant,
    ) : KHRecord(dataType = KHDataType.Weight)

    data class WheelChairPushes(
        val count: Long,
        val startTime: Instant,
        val endTime: Instant,
    ) : KHRecord(dataType = KHDataType.WheelChairPushes)
}
