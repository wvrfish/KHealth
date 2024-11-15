package com.khealth

internal fun String.extractHealthPermission(): String? {
    // Regex pattern to match Android health permissions
    val pattern = """(?:android|androidx)\.permission\.health\.[A-Z_]+""".toRegex()

    // Find and return the first match, or null if no match is found
    return pattern.find(this)?.value
}
