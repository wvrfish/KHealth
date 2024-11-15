package com.khealth

import kotlinx.datetime.Instant

data class KHCyclingPedalingCadenceSample(val revolutionsPerMinute: Double, val time: Instant)
