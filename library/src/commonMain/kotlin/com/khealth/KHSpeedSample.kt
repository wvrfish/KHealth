package com.khealth

import kotlinx.datetime.Instant

data class KHSpeedSample(val speed: KHUnit.Velocity, val time: Instant)
