package com.khealth

import co.touchlab.kermit.Logger

internal fun logDebug(message: String) {
    Logger.d { "[KHealth] -> $message" }
}

internal fun logError(throwable: Throwable) {
    Logger.e(throwable) { "[KHealth] -> ${throwable.message}" }
}
