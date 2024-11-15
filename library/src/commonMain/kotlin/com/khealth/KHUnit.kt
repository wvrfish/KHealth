package com.khealth

sealed class KHUnit {
    sealed class BloodGlucose : KHUnit() {
        data class MillimolesPerLiter(val value: Double) : BloodGlucose()
        data class MilligramsPerDeciliter(val value: Double) : BloodGlucose()
    }

    sealed class Energy : KHUnit() {
        data class Calorie(val value: Double) : Energy()
        data class KiloCalorie(val value: Double) : Energy()
        data class Joule(val value: Double) : Energy()
        data class KiloJoule(val value: Double) : Energy()
    }

    sealed class Length : KHUnit() {
        data class Meter(val value: Double) : Length()
        data class Mile(val value: Double) : Length()
        data class Inch(val value: Double) : Length()
    }

    sealed class Mass : KHUnit() {
        data class Gram(val value: Double) : Mass()
        data class Ounce(val value: Double) : Mass()
        data class Pound(val value: Double) : Mass()
    }

    sealed class Power : KHUnit() {
        data class KilocaloriePerDay(val value: Double) : Power()
        data class Watt(val value: Double) : Power()
    }

    sealed class Pressure : KHUnit() {
        data class MillimeterOfMercury(val value: Double) : Pressure()
    }

    sealed class Temperature : KHUnit() {
        data class Celsius(val value: Double) : Temperature()
        data class Fahrenheit(val value: Double) : Temperature()
    }

    sealed class Velocity : KHUnit() {
        data class MilesPerHour(val value: Double) : Velocity()
        data class MetersPerSecond(val value: Double) : Velocity()
        data class KilometersPerHour(val value: Double) : Velocity()
    }

    sealed class Volume : KHUnit() {
        data class Liter(val value: Double) : Volume()
        data class FluidOunceUS(val value: Double) : Volume()
    }
}
