package com.khealth

import kotlinx.datetime.Instant

data class KHHeartRateSample(val beatsPerMinute: Long, val time: Instant)
