package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.JVM

/**
 * Hold the search result.
 */
class ResultHolder {

    /**
     * The result.
     */
    val result: MutableList<JVM> = mutableListOf()

    /**
     * THe debug output.
     */
    private val debugOutput = StringBuilder()

    /**
     * Add a jvm
     */
    fun add(jvm: JVM) {
        result.add(jvm)
    }

    /**
     * Add all jvm
     */
    fun addAll(jvms: List<JVM>) {
        result.addAll(jvms)
    }

    /**
     * Append the debug message.
     */
    fun debug(obj: Any) {
        debugOutput.append(obj.toString())
        debugOutput.append("\n")
    }


    /**
     * Collect all debug message.
     */
    fun collectDebugMessages(): String {
        return debugOutput.toString()
    }

}