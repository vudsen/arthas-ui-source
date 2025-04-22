package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.ui.LocalJvmProviderForm
import io.github.vudsen.arthasui.bridge.util.InteractiveShell2ArthasProcessAdapter
import org.apache.commons.net.telnet.TelnetClient
import java.io.InputStream
import java.io.OutputStream

class LocalJvmProvider : JvmProvider {

    companion object {
        private val logger = Logger.getInstance(LocalJvmProvider::class.java)

        private class TelnetArthasProcess(private val client: TelnetClient) : ArthasProcess {
            override fun getInputStream(): InputStream {
                return client.inputStream
            }

            override fun getOutputStream(): OutputStream {
                return client.outputStream
            }

            override fun isAlive(): Boolean {
                return client.isAvailable
            }

            override fun stop(): Int {
                client.disconnect()
                return 0
            }
        }
    }

    override fun getName(): String {
        return "Local"
    }


    override fun searchJvm(template: HostMachineTemplate, providerConfig: JvmProviderConfig): List<JVM> {
        val hostMachine = template.getHostMachine()
        val config = providerConfig as LocalJvmProviderConfig
        val out: String = hostMachine.execute("${config.javaHome}/bin/jps", "-l").let {
            if (it.exitCode == 0) {
                it.stdout
            } else {
                template.grep("ps -eo pid,command", "java")
            }
        }

        val lines = out.split("\n")
        val result = ArrayList<JVM>(lines.size)

        for (line in lines) {
            val i = line.indexOf(' ')
            if (i < 0) {
                continue
            }
            val command = line.substring(i + 1)
            if (command.contains("grep java")) {
                continue
            }
            val pid = line.substring(0, i)

            result.add(LocalJVM(pid, command, JvmContext(template, providerConfig)))
        }
        return result
    }


    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig,
        toolchainManager: ToolchainManager
    ): ArthasBridgeFactory {
        val localJvmProviderConfig = jvmProviderConfig as LocalJvmProviderConfig
        val template = jvm.context.template
        val hostMachine = template.getHostMachine()

        val jattachHome = toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)
        val arthasHome = toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)

        // TODO, handle port not available
        if (hostMachine.getOS() != OS.WINDOWS) {
            return ArthasBridgeFactory {
                hostMachine.execute("$jattachHome/jattach", jvm.id, "load", "instrument", "false", "$arthasHome/arthas-agent.jar").ok()
                ArthasBridgeImpl(
                    InteractiveShell2ArthasProcessAdapter(
                        hostMachine.createInteractiveShell(
                            "${localJvmProviderConfig.javaHome}/bin/java",
                            "-jar",
                            "$arthasHome/arthas-client.jar"
                        )
                    )
                )
            }
        }

        return ArthasBridgeFactory {
            val r = hostMachine.execute("$jattachHome/jattach", jvm.id, "load", "instrument", "false", "$arthasHome/arthas-agent.jar").ok()
            println(r)
            val client = TelnetClient().apply {
                connectTimeout = 10000
            }
            logger.info("Trying to connect by telnet...")
            client.connect("127.0.0.1", 3658)
            logger.info("Telnet connect successfully!")
            return@ArthasBridgeFactory ArthasBridgeImpl(TelnetArthasProcess(client) as ArthasProcess)
        }
    }


    override fun createForm(
        oldState: JvmProviderConfig?,
        parentDisposable: Disposable
    ): FormComponent<JvmProviderConfig> {
        return LocalJvmProviderForm(oldState, parentDisposable)
    }

    override fun getConfigClass(): Class<out JvmProviderConfig> {
        return LocalJvmProviderConfig::class.java
    }

    override fun getJvmClass(): Class<out JVM> {
        return LocalJVM::class.java
    }

    override fun isJvmInactive(jvm: JVM): Boolean {
        val ctx = jvm.context
        return isPidNotExist(ctx.template.getHostMachine(), jvm.id)
    }

    override fun tryCreateDefaultConfiguration(template: HostMachineTemplate): JvmProviderConfig {
        template.env("JAVA_HOME") ?.let {
            return LocalJvmProviderConfig(true, it)
        }
        template.getHostMachine().execute("java", "-version") .let {
            if (it.exitCode == 0) {
                return LocalJvmProviderConfig(true, "java")
            }
        }
        return LocalJvmProviderConfig(false)
    }

    private fun isPidNotExist(hostMachine: HostMachine, pid: String): Boolean {
        when (hostMachine.getOS()) {
            OS.LINUX -> {
                val result = hostMachine.execute("ps", "-p", pid)
                if (result.exitCode != 0) {
                    return true
                }
                return !result.stdout.contains(pid)
            }

            OS.WINDOWS -> {
                val result = hostMachine.execute("tasklist", "/FI", "PID eq $pid")
                if (result.exitCode != 0) {
                    return true
                }
                return !result.stdout.contains(pid)
            }

            OS.MAC -> {
                // TODO
                return false
            }
        }

    }


}