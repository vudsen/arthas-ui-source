package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.JVM

class ResultHolder {

    /**
     * 结果集
     */
    val result: MutableList<JVM> = mutableListOf()

    /**
     * debug 输出
     */
    private val debugOutput = StringBuilder()

    fun add(jvm: JVM) {
        result.add(jvm)
    }

    /**
     * 输出 debug 消息
     */
    fun debug(obj: Any) {
        debugOutput.append(obj.toString())
        debugOutput.append("\n")
    }


    /**
     * 收集debug消息
     */
    fun collectDebugMessages(): String {
        return debugOutput.toString()
    }

}