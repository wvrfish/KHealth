package com.khealth

sealed class KHWriteResponse {
    data class Failed(val exception: Exception) : KHWriteResponse()
    data object SomeFailed : KHWriteResponse()
    data object Success : KHWriteResponse()
}
