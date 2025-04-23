package io.github.vudsen.arthasui.script.helper

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.providers.LocalJvmProvider

/**
 * A helper for local jvm search
 */
class LocalJvmSearchHelper(private val template: HostMachineTemplate, private val providerConfig: LocalJvmProviderConfig) {

    private val whiteSpacePattern = Regex(" +")

    private val ctx = JvmContext(template, providerConfig)

    /**
     * Find the jvm by port.
     * @param port The port
     * @param name Customize the jvm name. Will use the main class if it's null.
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
     * Find all JVM by command line args.
     * @param search The command line args that you want to match
     * @param name Customize the jvm name. Will use the main class if it's null.
     */
    @Suppress("unused")
    fun findByCommandLineArgs(search: String, name: String?): List<JVM> {
        val output: String = template.grep(search, "${providerConfig.javaHome}/bin/jps", "-lvm").let {
            if (it.exitCode == 0) {
                return@let it.stdout
            } else {
                return@let template.grep(arrayOf("java", search), "ps", "-eo", "pid,command").ok()
            }
        }

        val parseOutput = LocalJvmProvider.parseOutput(output, JvmContext(template, providerConfig))
        name ?.let {
            for (jvm in parseOutput) {
                jvm.name = it
            }
        }
        return parseOutput
    }

}