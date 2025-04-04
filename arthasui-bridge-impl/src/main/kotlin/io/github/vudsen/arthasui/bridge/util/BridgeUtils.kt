package io.github.vudsen.arthasui.bridge.util

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.bridge.bean.LocalJVM


object BridgeUtils {

    /**
     * grep 命令，兼容所有平台
     * @return 输出
     */
    fun grep(hostMachine: HostMachine, source: String, search: String): String {
        val result: CommandExecuteResult =  when(hostMachine.getOS()) {
            OS.LINUX -> hostMachine.execute("sh", "-c", "\"$source | grep ${search}\"")
            OS.WINDOWS -> hostMachine.execute("cmd", "/c", "\"$source | findstr \"${search}\"\"")
            OS.MAC -> TODO("Support MacOS")
        }
        // grep 没找到会返回 1
        if (result.exitCode == 0) {
            return result.stdout
        }
        if (result.exitCode == 1 && result.stdout == "") {
            return ""
        }
        // throw error.
        throw IllegalStateException("Failed to execute script: ${result.stdout}")
    }



}