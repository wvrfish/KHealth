package com.khealth

import kotlinx.datetime.Instant

data class KHSpeedSample(val unit: KHUnit.Velocity, val value: Double, val time: Instant)
