package com.khealth

import kotlinx.datetime.Instant

data class KHSleepStageSample(val stage: KHSleepStage, val startTime: Instant, val endTime: Instant)
