package io.github.vudsen.arthasui.script.helper

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig

/**
 * 提供所有可执行文件的路径
 */
class LocalJvmSearchHelper(private val template: HostMachineTemplate, private val providerConfig: LocalJvmProviderConfig) {

    private val whiteSpacePattern = Regex(" +")

    private val ctx = JvmContext(template, providerConfig)

    /**
     * 根据端口找到 jvm
     */
    @Suppress("unused")
    fun findByPort(port: Int, name: String?): List<JVM> {
        val hostMachine = template.getHostMachine()
        if (hostMachine.getOS() == OS.WINDOWS) {
            val result = hostMachine.execute("cmd", "/c", "\"netstat -ano | findstr :${port}\"").ok().split('\n')
            return result.map {
                val arr = it.trim().split(whiteSpacePattern)
                if (arr.size != 5) {
                    return@map null
                }
                val pid = arr[4]
                var actualName = name
                if (actualName == null) {
                    val taskNames =
                        hostMachine.execute("cmd", "/c", "\"tasklist | findstr ${pid}\"").ok().split(whiteSpacePattern)
                    if (taskNames.isEmpty()) {
                        return@map LocalJVM(pid, "<Unknown>", ctx)
                    }
                    actualName = taskNames[0]
                }
                return@map LocalJVM(pid, actualName, ctx)
            }.filterNotNull()
        } else if (hostMachine.getOS() == OS.LINUX) {
            val result = hostMachine.execute("sh", "-c", "\"netstat -tlpn | grep :${port}\"").ok().split('\n')
            return result.map {
                val arr = it.trim().split(whiteSpacePattern)
                if (arr.size != 7) {
                    return@map null
                }
                val pidAndName = arr[6].split('/')
                return@map LocalJVM(pidAndName[0], name ?: pidAndName[1], ctx)
            }.filterNotNull()
        } else {
            TODO("Support MacOS")
        }
    }

    /**
     * 根据命令行参数搜索 jvm
     */
    @Suppress("unused")
    fun findByCommandLineArgs(search: String, name: String?): List<JVM> {
        val jvms: List<String> = template.grep("\"${providerConfig.javaHome}/bin/jps\" -lvm", search).split('\n')
        val result = mutableListOf<JVM>()
        for (jvm in jvms) {
            if (!jvm.contains(search)) {
                continue
            }
            val i = jvm.indexOf(' ')
            val j = jvm.indexOf(' ', i + 1)

            val actualName = name ?: if (j < 0) jvm.substring(i + 1) else jvm.substring(i + 1, j)
            result.add(LocalJVM(jvm.substring(0, i), actualName, ctx));
        }
        return result
    }

}