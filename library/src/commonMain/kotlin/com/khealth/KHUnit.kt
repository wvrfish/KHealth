package com.khealth

sealed class KHUnit {
    sealed class Mass : KHUnit() {
        data class Gram(val value: Float) : Mass()
        data class Ounce(val value: Float) : Mass()
        data class Pound(val value: Float) : Mass()
    }

    sealed class Length : KHUnit() {
        data class Meter(val value: Float) : Length()
        data class Mile(val value: Float) : Length()
        data class Inch(val value: Float) : Length()
    }

    sealed class Volume : KHUnit() {
        data class Liter(val value: Float) : Volume()
        data class FluidOunceUS(val value: Float) : Volume()
    }

    sealed class Pressure : KHUnit() {
        data class MillimeterOfMercury(val value: Float) : Pressure()
    }

    sealed class Energy : KHUnit() {
        data class Joule(val value: Float) : Energy()
        data class Kilocalorie(val value: Float) : Energy()
    }

    sealed class Power : KHUnit() {
        data class Watt(val value: Float) : Power()
    }

    sealed class Temperature : KHUnit() {
        data class Celsius(val value: Float) : Temperature()
        data class Fahrenheit(val value: Float) : Temperature()
    }
}
