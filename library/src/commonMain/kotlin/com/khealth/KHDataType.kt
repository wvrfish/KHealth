package com.khealth

enum class KHDataType {
    /**
     * android.permission.health.READ_ACTIVE_CALORIES_BURNED
     * android.permission.health.WRITE_ACTIVE_CALORIES_BURNED
     */
    ActiveCaloriesBurned,

    /**
     * android.permission.health.READ_BASAL_METABOLIC_RATE
     * android.permission.health.WRITE_BASAL_METABOLIC_RATE
     */
    BasalMetabolicRate,

    /**
     * android.permission.health.READ_BLOOD_GLUCOSE
     * android.permission.health.WRITE_BLOOD_GLUCOSE
     */
    BloodGlucose,

    /**
     * android.permission.health.READ_BLOOD_PRESSURE
     * android.permission.health.WRITE_BLOOD_PRESSURE
     */
    BloodPressureSystolic,

    /**
     * android.permission.health.READ_BLOOD_PRESSURE
     * android.permission.health.WRITE_BLOOD_PRESSURE
     */
    BloodPressureDiastolic,

    /**
     * android.permission.health.READ_BODY_FAT
     * android.permission.health.WRITE_BODY_FAT
     */
    BodyFat,

    /**
     * android.permission.health.READ_BODY_TEMPERATURE
     * android.permission.health.WRITE_BODY_TEMPERATURE
     */
    BodyTemperature,

    /**
     * android.permission.health.READ_BODY_WATER_MASS
     * android.permission.health.WRITE_BODY_WATER_MASS
     */
    BodyWaterMass,

    /**
     * android.permission.health.READ_BONE_MASS
     * android.permission.health.WRITE_BONE_MASS
     */
    BoneMass,

    /**
     * android.permission.health.READ_CERVICAL_MUCUS
     * android.permission.health.WRITE_CERVICAL_MUCUS
     */
    CervicalMucus,

    /**
     * android.permission.health.READ_EXERCISE
     * android.permission.health.WRITE_EXERCISE
     */
    CyclingPedalingCadence,

    /**
     * android.permission.health.READ_DISTANCE
     * android.permission.health.WRITE_DISTANCE
     */
    Distance,

    /**
     * android.permission.health.READ_ELEVATION_GAINED
     * android.permission.health.WRITE_ELEVATION_GAINED
     */
    ElevationGained,

    /**
     * android.permission.health.READ_EXERCISE
     * android.permission.health.WRITE_EXERCISE
     */
    ExerciseSession,

    /**
     * android.permission.health.READ_FLOORS_CLIMBED
     * android.permission.health.WRITE_FLOORS_CLIMBED
     */
    FloorsClimbed,

    /**
     * android.permission.health.READ_HEART_RATE
     * android.permission.health.WRITE_HEART_RATE
     */
    HeartRate,

    /**
     * android.permission.health.READ_HEART_RATE_VARIABILITY
     * android.permission.health.WRITE_HEART_RATE_VARIABILITY
     */
    HeartRateVariability,

    /**
     * android.permission.health.READ_HEIGHT
     * android.permission.health.WRITE_HEIGHT
     */
    Height,

    /**
     * android.permission.health.READ_HYDRATION
     * android.permission.health.WRITE_HYDRATION
     */
    Hydration,

    /**
     * android.permission.health.READ_INTERMENSTRUAL_BLEEDING
     * android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING
     */
    IntermenstrualBleeding,

    /**
     * android.permission.health.READ_MENSTRUATION
     * android.permission.health.WRITE_MENSTRUATION
     */
    Menstruation,

    /**
     * android.permission.health.READ_LEAN_BODY_MASS
     * android.permission.health.WRITE_LEAN_BODY_MASS
     */
    LeanBodyMass,

    /**
     * android.permission.health.READ_MENSTRUATION
     * android.permission.health.WRITE_MENSTRUATION
     */
    MenstruationFlow,

    /**
     * android.permission.health.READ_OVULATION_TEST
     * android.permission.health.WRITE_OVULATION_TEST
     */
    OvulationTest,

    /**
     * android.permission.health.READ_OXYGEN_SATURATION
     * android.permission.health.WRITE_OXYGEN_SATURATION
     */
    OxygenSaturation,

    /**
     * android.permission.health.READ_POWER
     * android.permission.health.WRITE_POWER
     */
    Power,

    /**
     * android.permission.health.READ_RESPIRATORY_RATE
     * android.permission.health.WRITE_RESPIRATORY_RATE
     */
    RespiratoryRate,

    /**
     * android.permission.health.READ_RESTING_HEART_RATE
     * android.permission.health.WRITE_RESTING_HEART_RATE
     */
    RestingHeartRate,

    /**
     * android.permission.health.READ_SEXUAL_ACTIVITY
     * android.permission.health.WRITE_SEXUAL_ACTIVITY
     */
    SexualActivity,

    /**
     * android.permission.health.READ_SLEEP
     * android.permission.health.WRITE_SLEEP
     */
    SleepSession,

    /**
     * android.permission.health.READ_SPEED
     * android.permission.health.WRITE_SPEED
     */
    RunningSpeed,

    /**
     * android.permission.health.READ_SPEED
     * android.permission.health.WRITE_SPEED
     */
    CyclingSpeed,

    /**
     * android.permission.health.READ_STEPS
     * android.permission.health.WRITE_STEPS
     */
    StepCount,

    /**
     * android.permission.health.READ_VO2_MAX
     * android.permission.health.WRITE_VO2_MAX
     */
    Vo2Max,

    /**
     * android.permission.health.READ_WEIGHT
     * android.permission.health.WRITE_WEIGHT
     */
    Weight,

    /**
     * android.permission.health.READ_WHEELCHAIR_PUSHES
     * android.permission.health.WRITE_WHEELCHAIR_PUSHES
     */
    WheelChairPushes,
}
// TODO: Implement NutritionRecord as a common interface
