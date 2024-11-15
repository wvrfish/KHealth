package com.khealth

import kotlinx.datetime.Instant

data class KHPowerSample(val power: KHUnit.Power, val time: Instant)
