package com.khealth

import co.touchlab.kermit.Logger

internal fun logDebug(message: String) {
    Logger.d { "[KHealth] -> $message" }
}

internal fun logError(exception: Exception) {
    Logger.e(exception) { "[KHealth] -> ${exception.message}" }
}
