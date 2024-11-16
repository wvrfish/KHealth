package com.khealth

sealed class KHUnit {
    sealed class BloodGlucose : KHUnit() {
        data object MillimolesPerLiter : BloodGlucose()
        data object MilligramsPerDeciliter : BloodGlucose()
    }

    sealed class Energy : KHUnit() {
        data object Calorie : Energy()
        data object KiloCalorie : Energy()
        data object Joule : Energy()
        data object KiloJoule : Energy()
    }

    sealed class Length : KHUnit() {
        data object Meter : Length()
        data object Mile : Length()
        data object Inch : Length()
    }

    sealed class Mass : KHUnit() {
        data object Gram : Mass()
        data object Ounce : Mass()
        data object Pound : Mass()
    }

    sealed class Power : KHUnit() {
        data object KilocaloriePerDay : Power()
        data object Watt : Power()
    }

    sealed class Pressure : KHUnit() {
        data object MillimeterOfMercury : Pressure()
    }

    sealed class Temperature : KHUnit() {
        data object Celsius : Temperature()
        data object Fahrenheit : Temperature()
    }

    sealed class Velocity : KHUnit() {
        data object MilesPerHour : Velocity()
        data object MetersPerSecond : Velocity()
        data object KilometersPerHour : Velocity()
    }

    sealed class Volume : KHUnit() {
        data object Liter : Volume()
        data object FluidOunceUS : Volume()
    }
}
