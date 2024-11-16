package com.khealth

import kotlinx.datetime.Instant

data class KHPowerSample(val unit: KHUnit.Power, val value: Double, val time: Instant)
