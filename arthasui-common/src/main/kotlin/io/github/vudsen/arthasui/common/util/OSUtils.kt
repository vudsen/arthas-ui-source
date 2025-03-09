package io.github.vudsen.arthasui.common.util

object OSUtils {

    val isWindows = System.getProperty("os.name").startsWith("Windows");
    val isMacOS = System.getProperty("os.name").startsWith("Mac")
    val isLinux = System.getProperty("os.name").startsWith("Linux")
}