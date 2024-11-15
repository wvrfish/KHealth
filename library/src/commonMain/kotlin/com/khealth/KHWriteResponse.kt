package com.khealth

sealed class KHWriteResponse {
    data class Failed(val throwable: Throwable) : KHWriteResponse()

    // TODO: Check if you can return which records failed
    data object SomeFailed : KHWriteResponse()
    data object Success : KHWriteResponse()
}
