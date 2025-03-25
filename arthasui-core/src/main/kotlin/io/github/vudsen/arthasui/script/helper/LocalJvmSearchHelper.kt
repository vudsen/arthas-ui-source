package io.github.vudsen.arthasui.script.helper

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.util.BridgeUtils
import io.github.vudsen.arthasui.bridge.util.ok
import io.github.vudsen.arthasui.conf.HostMachineConfigV2

/**
 * 提供所有可执行文件的路径
 */
class LocalJvmSearchHelper(private val hostMachine: HostMachine, hostMachineConfig: HostMachineConfigV2) {

    private val whiteSpacePattern = Regex(" +")

    private var _provider: LocalJvmProviderConfig? = null

    init {
        for (entity in hostMachineConfig.providers) {
            if (entity is LocalJvmProviderConfig) {
                _provider = entity
                break
            }
        }
    }

    private fun getProvider(): LocalJvmProviderConfig {
        return _provider ?: throw IllegalStateException("LocalJvmProvider is configured.")
    }

    /**
     * 根据端口找到 jvm
     */
    @Suppress("unused")
    fun findByPort(port: Int): List<JVM> {
        if (hostMachine.getOS() == OS.WINDOWS) {
            val result = hostMachine.execute("cmd", "/c", "\"netstat -ano | findstr :${port}\"").ok().split('\n')
            return result.map {
                val arr = it.trim().split(whiteSpacePattern)
                if (arr.size != 5) {
                    return@map null
                }
                val pid = arr[4]
                val taskNames =
                    hostMachine.execute("cmd", "/c", "\"tasklist | findstr ${pid}\"").ok().split(whiteSpacePattern)
                if (taskNames.isEmpty()) {
                    return@map LocalJVM(pid, "<Unknown>")
                }
                return@map LocalJVM(pid, taskNames[0])
            }.filterNotNull()
        } else if (hostMachine.getOS() == OS.LINUX) {
            val result = hostMachine.execute("sh", "-c", "\"netstat -tlpn | grep :${port}\"").ok().split('\n')
            return result.map {
                val arr = it.trim().split(whiteSpacePattern)
                if (arr.size != 7) {
                    return@map null
                }
                val pidAndName = arr[6].split('/')
                return@map LocalJVM(pidAndName[0], pidAndName[1])
            }.filterNotNull()
        } else {
            TODO("Support MacOS")
        }
    }

    /**
     * 根据命令行参数搜索 jvm
     */
    @Suppress("unused")
    fun findByCommandLineArgs(search: String): List<JVM> {
        val provider = getProvider()
        val jvms: List<String> = BridgeUtils.grep(hostMachine, "\"${provider.jdkHome}/bin/jps\" -lvm", search).split('\n')
        val result = mutableListOf<JVM>()
        for (jvm in jvms) {
            if (!jvm.contains(search)) {
                continue
            }
            val i = jvm.indexOf(' ')
            val j = jvm.indexOf(' ', i + 1)

            val name = if (j < 0) jvm.substring(i + 1) else jvm.substring(i + 1, j)
            result.add(LocalJVM(jvm.substring(0, i), name));
        }
        return result
    }

}