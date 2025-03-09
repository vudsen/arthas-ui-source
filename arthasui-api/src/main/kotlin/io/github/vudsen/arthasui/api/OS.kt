package io.github.vudsen.arthasui.api

enum class OS {
    WINDOWS, MAC, LINUX
}

/**
 * 获取当前操纵系统
 */
fun currentOS(): OS {
    val property = System.getProperty("os.name")
    if (property.startsWith("Windows")) {
        return OS.WINDOWS
    } else if (property.startsWith("Mac")) {
        return OS.MAC
    } else if (property.startsWith("Linux")) {
        return OS.LINUX
    }
    throw IllegalArgumentException("Unknown OS: $property")
}
