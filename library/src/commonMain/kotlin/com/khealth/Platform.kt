package com.khealth

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform