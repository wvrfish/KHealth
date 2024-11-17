/*
 * Copyright (c) 2024 Shubham Singh
 *
 * This library is licensed under the Apache 2.0 License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khealth

/**
 * Depicts what kind of values a [KHRecord] refers to.
 */
enum class KHDataType {
    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.ActiveCaloriesBurned] records:
     * ```
     * android.permission.health.READ_ACTIVE_CALORIES_BURNED
     * android.permission.health.WRITE_ACTIVE_CALORIES_BURNED
     * ```
     * No actions required for Apple platforms.
     */
    ActiveCaloriesBurned,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BasalMetabolicRate] records:
     * ```
     * android.permission.health.READ_BASAL_METABOLIC_RATE
     * android.permission.health.WRITE_BASAL_METABOLIC_RATE
     * ```
     * No actions required for Apple platforms.
     */
    BasalMetabolicRate,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BloodGlucose] records:
     * ```
     * android.permission.health.READ_BLOOD_GLUCOSE
     * android.permission.health.WRITE_BLOOD_GLUCOSE
     * ```
     * No actions required for Apple platforms.
     */
    BloodGlucose,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BloodPressure] records:
     * ```
     * android.permission.health.READ_BLOOD_PRESSURE
     * android.permission.health.WRITE_BLOOD_PRESSURE
     * ```
     * No actions required for Apple platforms.
     */
    BloodPressure,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BodyFat] records:
     * ```
     * android.permission.health.READ_BODY_FAT
     * android.permission.health.WRITE_BODY_FAT
     * ```
     * No actions required for Apple platforms.
     */
    BodyFat,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BodyTemperature] records:
     * ```
     * android.permission.health.READ_BODY_TEMPERATURE
     * android.permission.health.WRITE_BODY_TEMPERATURE
     * ```
     * No actions required for Apple platforms.
     */
    BodyTemperature,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BodyWaterMass] records:
     * ```
     * android.permission.health.READ_BODY_WATER_MASS
     * android.permission.health.WRITE_BODY_WATER_MASS
     * ```
     * No actions required for Apple platforms.
     */
    BodyWaterMass,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.BoneMass] records:
     * ```
     * android.permission.health.READ_BONE_MASS
     * android.permission.health.WRITE_BONE_MASS
     * ```
     * No actions required for Apple platforms.
     */
    BoneMass,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.CervicalMucus] records:
     * ```
     * android.permission.health.READ_CERVICAL_MUCUS
     * android.permission.health.WRITE_CERVICAL_MUCUS
     * ```
     * No actions required for Apple platforms.
     */
    CervicalMucus,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.CyclingPedalingCadence] records:
     * ```
     * android.permission.health.READ_EXERCISE
     * android.permission.health.WRITE_EXERCISE
     * ```
     * No actions required for Apple platforms.
     */
    CyclingPedalingCadence,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Distance] records:
     * ```
     * android.permission.health.READ_DISTANCE
     * android.permission.health.WRITE_DISTANCE
     * ```
     * No actions required for Apple platforms.
     */
    Distance,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.ElevationGained] records:
     * ```
     * android.permission.health.READ_ELEVATION_GAINED
     * android.permission.health.WRITE_ELEVATION_GAINED
     * ```
     * No actions required for Apple platforms.
     */
    ElevationGained,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.FloorsClimbed] records:
     * ```
     * android.permission.health.READ_FLOORS_CLIMBED
     * android.permission.health.WRITE_FLOORS_CLIMBED
     * ```
     * No actions required for Apple platforms.
     */
    FloorsClimbed,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.HeartRate] records:
     * ```
     * android.permission.health.READ_HEART_RATE
     * android.permission.health.WRITE_HEART_RATE
     * ```
     * No actions required for Apple platforms.
     */
    HeartRate,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.HeartRateVariability] records:
     * ```
     * android.permission.health.READ_HEART_RATE_VARIABILITY
     * android.permission.health.WRITE_HEART_RATE_VARIABILITY
     * ```
     * No actions required for Apple platforms.
     */
    HeartRateVariability,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Height] records:
     * ```
     * android.permission.health.READ_HEIGHT
     * android.permission.health.WRITE_HEIGHT
     * ```
     * No actions required for Apple platforms.
     */
    Height,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Hydration] records:
     * ```
     * android.permission.health.READ_HYDRATION
     * android.permission.health.WRITE_HYDRATION
     * ```
     * No actions required for Apple platforms.
     */
    Hydration,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.IntermenstrualBleeding] records:
     * ```
     * android.permission.health.READ_INTERMENSTRUAL_BLEEDING
     * android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING
     * ```
     * No actions required for Apple platforms.
     */
    IntermenstrualBleeding,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.LeanBodyMass] records:
     * ```
     * android.permission.health.READ_LEAN_BODY_MASS
     * android.permission.health.WRITE_LEAN_BODY_MASS
     * ```
     * No actions required for Apple platforms.
     */
    LeanBodyMass,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.MenstruationPeriod] records:
     * ```
     * android.permission.health.READ_MENSTRUATION
     * android.permission.health.WRITE_MENSTRUATION
     * ```
     * No actions required for Apple platforms.
     */
    MenstruationPeriod,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.MenstruationFlow] records:
     * ```
     * android.permission.health.READ_MENSTRUATION
     * android.permission.health.WRITE_MENSTRUATION
     * ```
     * No actions required for Apple platforms.
     */
    MenstruationFlow,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.OvulationTest] records:
     * ```
     * android.permission.health.READ_OVULATION_TEST
     * android.permission.health.WRITE_OVULATION_TEST
     * ```
     * No actions required for Apple platforms.
     */
    OvulationTest,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.OxygenSaturation] records:
     * ```
     * android.permission.health.READ_OXYGEN_SATURATION
     * android.permission.health.WRITE_OXYGEN_SATURATION
     * ```
     * No actions required for Apple platforms.
     */
    OxygenSaturation,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Power] records:
     * ```
     * android.permission.health.READ_POWER
     * android.permission.health.WRITE_POWER
     * ```
     * No actions required for Apple platforms.
     */
    Power,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.RespiratoryRate] records:
     * ```
     * android.permission.health.READ_RESPIRATORY_RATE
     * android.permission.health.WRITE_RESPIRATORY_RATE
     * ```
     * No actions required for Apple platforms.
     */
    RespiratoryRate,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.RestingHeartRate] records:
     * ```
     * android.permission.health.READ_RESTING_HEART_RATE
     * android.permission.health.WRITE_RESTING_HEART_RATE
     * ```
     * No actions required for Apple platforms.
     */
    RestingHeartRate,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.SexualActivity] records:
     * ```
     * android.permission.health.READ_SEXUAL_ACTIVITY
     * android.permission.health.WRITE_SEXUAL_ACTIVITY
     * ```
     * No actions required for Apple platforms.
     */
    SexualActivity,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.SleepSession] records:
     * ```
     * android.permission.health.READ_SLEEP
     * android.permission.health.WRITE_SLEEP
     * ```
     * No actions required for Apple platforms.
     */
    SleepSession,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Speed] records:
     * ```
     * android.permission.health.READ_SPEED
     * android.permission.health.WRITE_SPEED
     * ```
     * No actions required for Apple platforms.
     */
    Speed,

    RunningSpeed,

    CyclingSpeed,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.StepCount] records:
     * ```
     * android.permission.health.READ_STEPS
     * android.permission.health.WRITE_STEPS
     * ```
     * No actions required for Apple platforms.
     */
    StepCount,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Vo2Max] records:
     * ```
     * android.permission.health.READ_VO2_MAX
     * android.permission.health.WRITE_VO2_MAX
     * ```
     * No actions required for Apple platforms.
     */
    Vo2Max,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.Weight] records:
     * ```
     * android.permission.health.READ_WEIGHT
     * android.permission.health.WRITE_WEIGHT
     * ```
     * No actions required for Apple platforms.
     */
    Weight,

    /**
     * For Android, add one or both the following permissions in `AndroidManifest.xml` to be able to
     * Read/Write [KHRecord.WheelChairPushes] records:
     * ```
     * android.permission.health.READ_WHEELCHAIR_PUSHES
     * android.permission.health.WRITE_WHEELCHAIR_PUSHES
     * ```
     * No actions required for Apple platforms.
     */
    WheelChairPushes,
}
// TODO: Implement NutritionRecord as a common interface
//  /**
//   * android.permission.health.READ_NUTRITION
//   * android.permission.health.WRITE_NUTRITION
//   */

// TODO: Implement ExerciseRecord as a common interface
//  /**
//   * android.permission.health.READ_EXERCISE
//   * android.permission.health.WRITE_EXERCISE
//   */
