package com.khealth

import kotlinx.datetime.Instant

data class KHRecord<T : KHUnit>(
    val unitValue: T,
    val startDateTime: Instant,
    val endDateTime: Instant,
)
