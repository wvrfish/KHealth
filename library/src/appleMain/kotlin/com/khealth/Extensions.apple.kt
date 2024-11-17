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

import kotlinx.cinterop.UnsafeNumber
import platform.HealthKit.HKCategoryValueCervicalMucusQuality
import platform.HealthKit.HKCategoryValueCervicalMucusQualityCreamy
import platform.HealthKit.HKCategoryValueCervicalMucusQualityDry
import platform.HealthKit.HKCategoryValueCervicalMucusQualityEggWhite
import platform.HealthKit.HKCategoryValueCervicalMucusQualitySticky
import platform.HealthKit.HKCategoryValueCervicalMucusQualityWatery
import platform.HealthKit.HKCategoryValueMenstrualFlowHeavy
import platform.HealthKit.HKCategoryValueMenstrualFlowLight
import platform.HealthKit.HKCategoryValueMenstrualFlowMedium
import platform.HealthKit.HKCategoryValueMenstrualFlowUnspecified
import platform.HealthKit.HKCategoryValueOvulationTestResultEstrogenSurge
import platform.HealthKit.HKCategoryValueOvulationTestResultIndeterminate
import platform.HealthKit.HKCategoryValueOvulationTestResultNegative
import platform.HealthKit.HKCategoryValueOvulationTestResultPositive
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepCore
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepDeep
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepREM
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepUnspecified
import platform.HealthKit.HKCategoryValueSleepAnalysisAwake
import platform.HealthKit.HKMetricPrefixDeci
import platform.HealthKit.HKMetricPrefixKilo
import platform.HealthKit.HKMetricPrefixMilli
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKUnit
import platform.HealthKit.HKUnitMolarMassBloodGlucose
import platform.HealthKit.countUnit
import platform.HealthKit.dayUnit
import platform.HealthKit.degreeCelsiusUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.fluidOunceUSUnit
import platform.HealthKit.gramUnit
import platform.HealthKit.gramUnitWithMetricPrefix
import platform.HealthKit.hourUnit
import platform.HealthKit.inchUnit
import platform.HealthKit.jouleUnit
import platform.HealthKit.jouleUnitWithMetricPrefix
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.largeCalorieUnit
import platform.HealthKit.literUnit
import platform.HealthKit.literUnitWithMetricPrefix
import platform.HealthKit.meterUnit
import platform.HealthKit.meterUnitWithMetricPrefix
import platform.HealthKit.mileUnit
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.moleUnitWithMetricPrefix
import platform.HealthKit.ounceUnit
import platform.HealthKit.percentUnit
import platform.HealthKit.poundUnit
import platform.HealthKit.secondUnit
import platform.HealthKit.secondUnitWithMetricPrefix
import platform.HealthKit.smallCalorieUnit
import platform.HealthKit.unitDividedByUnit
import platform.HealthKit.wattUnit
import platform.darwin.NSInteger

@OptIn(UnsafeNumber::class)
internal object AppleUnits {
    val millimolesPerLiter = HKUnit
        .moleUnitWithMetricPrefix(HKMetricPrefixMilli, HKUnitMolarMassBloodGlucose)
        .unitDividedByUnit(HKUnit.literUnit())

    val milligramsPerDeciliter = HKUnit
        .gramUnitWithMetricPrefix(HKMetricPrefixMilli)
        .unitDividedByUnit(HKUnit.literUnitWithMetricPrefix(HKMetricPrefixDeci))

    val calorie = HKUnit.smallCalorieUnit()
    val joule = HKUnit.jouleUnit()
    val kilocalorie = HKUnit.largeCalorieUnit()
    val kilojoule = HKUnit.jouleUnitWithMetricPrefix(HKMetricPrefixKilo)
    val inch = HKUnit.inchUnit()
    val meter = HKUnit.meterUnit()
    val mile = HKUnit.mileUnit()
    val gram = HKUnit.gramUnit()
    val ounce = HKUnit.ounceUnit()
    val pound = HKUnit.poundUnit()

    val kilometersPerHour = HKUnit
        .meterUnitWithMetricPrefix(HKMetricPrefixKilo)
        .unitDividedByUnit(HKUnit.hourUnit())

    val metersPerSecond = HKUnit.meterUnit().unitDividedByUnit(HKUnit.secondUnit())
    val milesPerHour = HKUnit.mileUnit().unitDividedByUnit(HKUnit.hourUnit())
    val millimeterOfMercury = HKUnit.millimeterOfMercuryUnit()
    val celsius = HKUnit.degreeCelsiusUnit()
    val fahrenheit = HKUnit.degreeFahrenheitUnit()
    val beatsPerMinute = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())
    val millisecond = HKUnit.secondUnitWithMetricPrefix(HKMetricPrefixMilli)
    val fluidOunceUS = HKUnit.fluidOunceUSUnit()
    val liter = HKUnit.literUnit()
    val percent = HKUnit.percentUnit()
    val kilocaloriePerDay = HKUnit.kilocalorieUnit().unitDividedByUnit(HKUnit.dayUnit())
    val watt = HKUnit.wattUnit()
    val count = HKUnit.countUnit()
    val vo2Max = HKUnit.unitFromString("ml/kg*min")
}

internal infix fun KHUnit.BloodGlucose.toNativeBloodGlucoseFor(value: Double): HKQuantity {
    return when (this) {
        is KHUnit.BloodGlucose.MillimolesPerLiter -> HKQuantity.quantityWithUnit(
            unit = AppleUnits.millimolesPerLiter,
            doubleValue = value
        )

        is KHUnit.BloodGlucose.MilligramsPerDeciliter -> HKQuantity.quantityWithUnit(
            unit = AppleUnits.milligramsPerDeciliter,
            doubleValue = value
        )
    }
}

internal infix fun HKQuantity.toDoubleValueFor(glucose: KHUnit.BloodGlucose): Double {
    return when (glucose) {
        KHUnit.BloodGlucose.MilligramsPerDeciliter -> doubleValueForUnit(AppleUnits.millimolesPerLiter)
        KHUnit.BloodGlucose.MillimolesPerLiter -> doubleValueForUnit(AppleUnits.milligramsPerDeciliter)
    }
}

internal infix fun KHUnit.Energy.toNativeEnergyFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Energy.Calorie ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.calorie, doubleValue = value)

    is KHUnit.Energy.Joule ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.joule, doubleValue = value)

    is KHUnit.Energy.KiloCalorie ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.kilocalorie, doubleValue = value)

    is KHUnit.Energy.KiloJoule ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.kilojoule, doubleValue = value)
}

internal infix fun HKQuantity.toDoubleValueFor(energy: KHUnit.Energy): Double = when (energy) {
    KHUnit.Energy.Calorie -> this.doubleValueForUnit(AppleUnits.calorie)
    KHUnit.Energy.Joule -> this.doubleValueForUnit(AppleUnits.joule)
    KHUnit.Energy.KiloCalorie -> this.doubleValueForUnit(AppleUnits.kilocalorie)
    KHUnit.Energy.KiloJoule -> this.doubleValueForUnit(AppleUnits.kilojoule)
}

internal infix fun KHUnit.Length.toNativeLengthFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Length.Inch -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.inch,
        doubleValue = value
    )

    is KHUnit.Length.Meter -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.meter,
        doubleValue = value
    )

    is KHUnit.Length.Mile -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.mile,
        doubleValue = value
    )
}

internal infix fun HKQuantity.toDoubleValueFor(length: KHUnit.Length): Double = when (length) {
    KHUnit.Length.Inch -> doubleValueForUnit(AppleUnits.inch)
    KHUnit.Length.Meter -> doubleValueForUnit(AppleUnits.meter)
    KHUnit.Length.Mile -> doubleValueForUnit(AppleUnits.mile)
}

internal infix fun KHUnit.Mass.toNativeMassFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Mass.Gram -> HKQuantity.quantityWithUnit(unit = AppleUnits.gram, doubleValue = value)
    is KHUnit.Mass.Ounce -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.ounce,
        doubleValue = value
    )

    is KHUnit.Mass.Pound -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.pound,
        doubleValue = value
    )
}

internal infix fun HKQuantity.toDoubleValueFor(mass: KHUnit.Mass): Double = when (mass) {
    KHUnit.Mass.Gram -> doubleValueForUnit(AppleUnits.gram)
    KHUnit.Mass.Ounce -> doubleValueForUnit(AppleUnits.ounce)
    KHUnit.Mass.Pound -> doubleValueForUnit(AppleUnits.pound)
}

internal infix fun KHUnit.Power.toNativePowerFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Power.KilocaloriePerDay -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.kilocaloriePerDay,
        doubleValue = value,
    )

    is KHUnit.Power.Watt -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.watt,
        doubleValue = value,
    )
}

internal infix fun HKQuantity.toDoubleValueFor(power: KHUnit.Power): Double = when (power) {
    KHUnit.Power.KilocaloriePerDay -> doubleValueForUnit(AppleUnits.kilocaloriePerDay)
    KHUnit.Power.Watt -> doubleValueForUnit(AppleUnits.watt)
}

internal infix fun KHUnit.Temperature.toNativeTemperatureFor(value: Double): HKQuantity {
    return when (this) {
        is KHUnit.Temperature.Celsius ->
            HKQuantity.quantityWithUnit(unit = AppleUnits.celsius, doubleValue = value)

        is KHUnit.Temperature.Fahrenheit ->
            HKQuantity.quantityWithUnit(unit = AppleUnits.fahrenheit, doubleValue = value)
    }
}

internal infix fun HKQuantity.toDoubleValueFor(temperature: KHUnit.Temperature): Double {
    return when (temperature) {
        KHUnit.Temperature.Celsius -> doubleValueForUnit(AppleUnits.celsius)
        KHUnit.Temperature.Fahrenheit -> doubleValueForUnit(AppleUnits.fahrenheit)
    }
}

internal infix fun KHUnit.Velocity.toNativeVelocityFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Velocity.KilometersPerHour ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.kilometersPerHour, doubleValue = value)

    is KHUnit.Velocity.MetersPerSecond ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.metersPerSecond, doubleValue = value)

    is KHUnit.Velocity.MilesPerHour ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.milesPerHour, doubleValue = value)
}

internal infix fun HKQuantity.toDoubleValueFor(velocity: KHUnit.Velocity): Double {
    return when (velocity) {
        KHUnit.Velocity.KilometersPerHour -> doubleValueForUnit(AppleUnits.kilometersPerHour)
        KHUnit.Velocity.MetersPerSecond -> doubleValueForUnit(AppleUnits.metersPerSecond)
        KHUnit.Velocity.MilesPerHour -> doubleValueForUnit(AppleUnits.milesPerHour)
    }
}

internal infix fun KHUnit.Pressure.toNativePressureFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Pressure.MillimeterOfMercury ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.millimeterOfMercury, doubleValue = value)
}

internal infix fun HKQuantity.toDoubleValueFor(pressure: KHUnit.Pressure): Double {
    return when (pressure) {
        KHUnit.Pressure.MillimeterOfMercury -> doubleValueForUnit(AppleUnits.millimeterOfMercury)
    }
}

internal infix fun KHUnit.Volume.toNativeVolumeFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Volume.FluidOunceUS -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.fluidOunceUS,
        doubleValue = value,
    )

    is KHUnit.Volume.Liter -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.liter,
        doubleValue = value,
    )
}

internal infix fun HKQuantity.toDoubleValueFor(volume: KHUnit.Volume): Double {
    return when (volume) {
        KHUnit.Volume.FluidOunceUS -> doubleValueForUnit(AppleUnits.fluidOunceUS)
        KHUnit.Volume.Liter -> doubleValueForUnit(AppleUnits.liter)
    }
}

@OptIn(UnsafeNumber::class)
internal fun KHCervicalMucusAppearance.toNativeCervicalMucusQuality(): HKCategoryValueCervicalMucusQuality {
    return when (this) {
        KHCervicalMucusAppearance.Creamy -> HKCategoryValueCervicalMucusQualityCreamy
        KHCervicalMucusAppearance.Dry -> HKCategoryValueCervicalMucusQualityDry
        KHCervicalMucusAppearance.EggWhite -> HKCategoryValueCervicalMucusQualityEggWhite
        KHCervicalMucusAppearance.Sticky -> HKCategoryValueCervicalMucusQualitySticky
        KHCervicalMucusAppearance.Watery -> HKCategoryValueCervicalMucusQualityWatery
    }
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHCervicalMucusAppearance(): KHCervicalMucusAppearance = when (this) {
    HKCategoryValueCervicalMucusQualityCreamy -> KHCervicalMucusAppearance.Creamy
    HKCategoryValueCervicalMucusQualityDry -> KHCervicalMucusAppearance.Dry
    HKCategoryValueCervicalMucusQualityEggWhite -> KHCervicalMucusAppearance.EggWhite
    HKCategoryValueCervicalMucusQualitySticky -> KHCervicalMucusAppearance.Sticky
    HKCategoryValueCervicalMucusQualityWatery -> KHCervicalMucusAppearance.Watery
    else -> throw IllegalStateException("Unknown Cervical Mucus Appearance!")
}

@OptIn(UnsafeNumber::class)
internal fun KHMenstruationFlowType.toNativeMenstrualFlow(): NSInteger {
    return when (this) {
        KHMenstruationFlowType.Unknown -> HKCategoryValueMenstrualFlowUnspecified
        KHMenstruationFlowType.Light -> HKCategoryValueMenstrualFlowLight
        KHMenstruationFlowType.Medium -> HKCategoryValueMenstrualFlowMedium
        KHMenstruationFlowType.Heavy -> HKCategoryValueMenstrualFlowHeavy
    }
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHMenstruationFlowType(): KHMenstruationFlowType = when (this) {
    HKCategoryValueMenstrualFlowUnspecified -> KHMenstruationFlowType.Unknown
    HKCategoryValueMenstrualFlowLight -> KHMenstruationFlowType.Light
    HKCategoryValueMenstrualFlowMedium -> KHMenstruationFlowType.Medium
    HKCategoryValueMenstrualFlowHeavy -> KHMenstruationFlowType.Heavy
    else -> throw IllegalStateException("Unknown Menstrual Flow type!")
}

@OptIn(UnsafeNumber::class)
internal fun KHOvulationTestResult.toNativeOvulationResult(): NSInteger {
    return when (this) {
        KHOvulationTestResult.High -> HKCategoryValueOvulationTestResultEstrogenSurge
        KHOvulationTestResult.Negative -> HKCategoryValueOvulationTestResultNegative
        KHOvulationTestResult.Positive -> HKCategoryValueOvulationTestResultPositive
        KHOvulationTestResult.Inconclusive -> HKCategoryValueOvulationTestResultIndeterminate
    }
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHOvulationTestResult(): KHOvulationTestResult = when (this) {
    HKCategoryValueOvulationTestResultEstrogenSurge -> KHOvulationTestResult.High
    HKCategoryValueOvulationTestResultNegative -> KHOvulationTestResult.Negative
    HKCategoryValueOvulationTestResultPositive -> KHOvulationTestResult.Positive
    HKCategoryValueOvulationTestResultIndeterminate -> KHOvulationTestResult.Inconclusive
    else -> throw IllegalStateException("Unknown Ovulation Test result!")
}

@OptIn(UnsafeNumber::class)
internal fun KHSleepStage.toNativeSleepStage(): NSInteger = when (this) {
    KHSleepStage.Awake,
    KHSleepStage.AwakeInBed,
    KHSleepStage.AwakeOutOfBed -> HKCategoryValueSleepAnalysisAwake

    KHSleepStage.Deep -> HKCategoryValueSleepAnalysisAsleepDeep
    KHSleepStage.Light -> HKCategoryValueSleepAnalysisAsleepCore
    KHSleepStage.REM -> HKCategoryValueSleepAnalysisAsleepREM

    KHSleepStage.Sleeping,
    KHSleepStage.Unknown -> HKCategoryValueSleepAnalysisAsleepUnspecified
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHSleepStage(): KHSleepStage = when (this) {
    HKCategoryValueSleepAnalysisAwake -> KHSleepStage.Awake
    HKCategoryValueSleepAnalysisAsleepDeep -> KHSleepStage.Deep
    HKCategoryValueSleepAnalysisAsleepCore -> KHSleepStage.Light
    HKCategoryValueSleepAnalysisAsleepREM -> KHSleepStage.REM
    HKCategoryValueSleepAnalysisAsleepUnspecified -> KHSleepStage.Sleeping
    else -> throw IllegalStateException("Unknown Sleep stage!")
}
