package com.khealth

import co.touchlab.kermit.Logger

internal fun logDebug(exception: Throwable) {
    Logger.d { "[KHealth] -> ${exception.message}" }
}

internal fun logError(exception: Throwable) {
    Logger.e(exception) { "[KHealth] -> ${exception.message}" }
}
