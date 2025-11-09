package com.khealth

import kotlin.time.Instant

enum class KHBiologicalSex { Male, Female, Other }

enum class KHCharacteristicType {
    BiologicalSex,
    DateOfBirth
}

sealed class KHCharacteristicRecord {
    data class BiologicalSex(
        val value: KHBiologicalSex
    ) : KHCharacteristicRecord()

    data class DateOfBirth(
        val dateOfBirth: Instant
    ) : KHCharacteristicRecord()

}