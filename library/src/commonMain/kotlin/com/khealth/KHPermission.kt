package com.khealth

sealed class KHPermission(
    internal val readRequested: Boolean,
    internal val writeRequested: Boolean,
) {
    /**
     * android.permission.health.READ_ACTIVE_CALORIES_BURNED
     * android.permission.health.WRITE_ACTIVE_CALORIES_BURNED
     */
    data class ActiveCaloriesBurned(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BASAL_METABOLIC_RATE
     * android.permission.health.WRITE_BASAL_METABOLIC_RATE
     */
    data class BasalMetabolicRate(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BLOOD_GLUCOSE
     * android.permission.health.WRITE_BLOOD_GLUCOSE
     */
    data class BloodGlucose(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BLOOD_PRESSURE
     * android.permission.health.WRITE_BLOOD_PRESSURE
     */
    data class BloodPressureSystolic(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BLOOD_PRESSURE
     * android.permission.health.WRITE_BLOOD_PRESSURE
     */
    data class BloodPressureDiastolic(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BODY_FAT
     * android.permission.health.WRITE_BODY_FAT
     */
    data class BodyFat(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BODY_TEMPERATURE
     * android.permission.health.WRITE_BODY_TEMPERATURE
     */
    data class BodyTemperature(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BODY_WATER_MASS
     * android.permission.health.WRITE_BODY_WATER_MASS
     */
    data class BodyWaterMass(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_BONE_MASS
     * android.permission.health.WRITE_BONE_MASS
     */
    data class BoneMass(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_CERVICAL_MUCUS
     * android.permission.health.WRITE_CERVICAL_MUCUS
     */
    data class CervicalMucus(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_EXERCISE
     * android.permission.health.WRITE_EXERCISE
     */
    data class CyclingPedalingCadence(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_DISTANCE
     * android.permission.health.WRITE_DISTANCE
     */
    data class Distance(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_ELEVATION_GAINED
     * android.permission.health.WRITE_ELEVATION_GAINED
     */
    data class ElevationGained(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_EXERCISE
     * android.permission.health.WRITE_EXERCISE
     */
    data class ExerciseSession(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_FLOORS_CLIMBED
     * android.permission.health.WRITE_FLOORS_CLIMBED
     */
    data class FloorsClimbed(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_HEART_RATE
     * android.permission.health.WRITE_HEART_RATE
     */
    data class HeartRate(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_HEART_RATE_VARIABILITY
     * android.permission.health.WRITE_HEART_RATE_VARIABILITY
     */
    data class HeartRateVariability(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_HEIGHT
     * android.permission.health.WRITE_HEIGHT
     */
    data class Height(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_HYDRATION
     * android.permission.health.WRITE_HYDRATION
     */
    data class Hydration(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_INTERMENSTRUAL_BLEEDING
     * android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING
     */
    data class IntermenstrualBleeding(val read: Boolean, val write: Boolean) :
    KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_MENSTRUATION
     * android.permission.health.WRITE_MENSTRUATION
     */
    data class Menstruation(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_LEAN_BODY_MASS
     * android.permission.health.WRITE_LEAN_BODY_MASS
     */
    data class LeanBodyMass(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_MENSTRUATION
     * android.permission.health.WRITE_MENSTRUATION
     */
    data class MenstruationFlow(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_OVULATION_TEST
     * android.permission.health.WRITE_OVULATION_TEST
     */
    data class OvulationTest(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_OXYGEN_SATURATION
     * android.permission.health.WRITE_OXYGEN_SATURATION
     */
    data class OxygenSaturation(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_POWER
     * android.permission.health.WRITE_POWER
     */
    data class Power(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_RESPIRATORY_RATE
     * android.permission.health.WRITE_RESPIRATORY_RATE
     */
    data class RespiratoryRate(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_RESTING_HEART_RATE
     * android.permission.health.WRITE_RESTING_HEART_RATE
     */
    data class RestingHeartRate(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_SEXUAL_ACTIVITY
     * android.permission.health.WRITE_SEXUAL_ACTIVITY
     */
    data class SexualActivity(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_SLEEP
     * android.permission.health.WRITE_SLEEP
     */
    data class SleepSession(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_SPEED
     * android.permission.health.WRITE_SPEED
     */
    data class RunningSpeed(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_SPEED
     * android.permission.health.WRITE_SPEED
     */
    data class CyclingSpeed(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_STEPS
     * android.permission.health.WRITE_STEPS
     */
    data class StepCount(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_VO2_MAX
     * android.permission.health.WRITE_VO2_MAX
     */
    data class Vo2Max(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_WEIGHT
     * android.permission.health.WRITE_WEIGHT
     */
    data class Weight(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)

    /**
     * android.permission.health.READ_WHEELCHAIR_PUSHES
     * android.permission.health.WRITE_WHEELCHAIR_PUSHES
     */
    data class WheelChairPushes(val read: Boolean, val write: Boolean) :
        KHPermission(readRequested = read, writeRequested = write)
}

// TODO: Implement NutritionRecord as a common interface
