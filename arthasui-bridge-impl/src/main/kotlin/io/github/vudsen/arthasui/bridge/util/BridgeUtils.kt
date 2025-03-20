package io.github.vudsen.arthasui.bridge.util

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.bridge.bean.LocalJVM


object BridgeUtils {

    fun parseJpsOutput(out: String): MutableList<JVM> {
        val lines = out.split("\n")
        val result = ArrayList<JVM>(lines.size)

        for (line in lines) {
            val split = line.split(" ")
            if (split.isEmpty()) {
                continue
            } else if (split.size == 1) {
                result.add(LocalJVM(split[0].trim(), "<null>"))
            } else if (split.size == 2) {
                result.add(LocalJVM(split[0].trim(), split[1].trim()))
            } else {
                throw IllegalStateException("Unreachable code.")
            }
        }
        return result
    }


}