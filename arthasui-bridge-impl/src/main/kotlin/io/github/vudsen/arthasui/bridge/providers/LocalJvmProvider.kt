package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerUtil
import io.github.vudsen.arthasui.bridge.toolchain.DefaultToolChainManager
import io.github.vudsen.arthasui.bridge.ui.LocalJvmProviderForm
import io.github.vudsen.arthasui.common.ArthasUIIcons
import org.apache.commons.net.telnet.TelnetClient
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import javax.swing.Icon

class LocalJvmProvider : JvmProvider {

    companion object {
        private val logger = Logger.getInstance(LocalJvmProvider::class.java)

        private class TelnetArthasProcess(private val client: TelnetClient) : InteractiveShell {

            private val reader = InputStreamReader(client.inputStream)

            private val writer = OutputStreamWriter(client.outputStream)

            override fun getReader(): Reader {
                return reader
            }

            override fun getWriter(): Writer {
                return writer
            }

            override fun isAlive(): Boolean {
                return client.isAvailable
            }

            override fun exitCode(): Int? {
                return 0
            }

            override fun close() {
                client.disconnect()
                reader.close()
                writer.close()
            }

        }

        @JvmStatic
        fun parseOutput(output: String, ctx: JvmContext): List<JVM> {
            val lines = output.split("\n")
            val result = ArrayList<JVM>(lines.size)

            for (l in lines) {
                val line = l.trim()
                val i = line.indexOf(' ')
                if (i < 0) {
                    continue
                }
                val command = line.substring(i + 1)
                // 过滤掉 Jps 是因为集成测试不好选中树节点
                if (command.contains("grep java") || command.endsWith("Jps")) {
                    continue
                }
                val pid = line.substring(0, i)

                result.add(LocalJVM(pid, command, ctx))
            }
            return result
        }
    }

    override fun getName(): String {
        return "Local"
    }


    override fun searchJvm(hostMachine: HostMachine, providerConfig: JvmProviderConfig): JvmSearchResult {
        if (hostMachine !is ShellAvailableHostMachine) {
            return JvmSearchResult(mutableListOf())
        }
        val config = providerConfig as LocalJvmProviderConfig
        val out: String = hostMachine.execute("${config.javaHome}/bin/jps", "-l").let {
            if (it.exitCode == 0) {
                return@let it.stdout
            } else {
                return@let hostMachine.grep("java", "ps", "-eo", "pid,command").ok()
            }
        }

        return JvmSearchResult(parseOutput(out, JvmContext(hostMachine, providerConfig)))
    }


    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        val localJvmProviderConfig = jvmProviderConfig as LocalJvmProviderConfig
        val hostMachine = jvm.context.getHostMachineAsShellAvailable()
        val toolchainManager = ToolChainManagerUtil.createToolChainManager(hostMachine)

        val jattachHome = toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)
        val arthasHome = toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)

        // TODO, handle port not available
        if (hostMachine.getOS() != OS.WINDOWS) {
            return ArthasBridgeFactory {
                hostMachine.execute("$jattachHome/jattach", jvm.id, "load", "instrument", "false", "$arthasHome/arthas-agent.jar").ok()
                ArthasBridgeImpl(
                    hostMachine.createInteractiveShell(
                        "${localJvmProviderConfig.javaHome}/bin/java",
                        "-jar",
                        "$arthasHome/arthas-client.jar"
                    )
                )
            }
        }

        return ArthasBridgeFactory {
            hostMachine.execute("$jattachHome/jattach", jvm.id, "load", "instrument", "false", "$arthasHome/arthas-agent.jar").ok()
            val client = TelnetClient().apply {
                connectTimeout = 10000
            }
            logger.info("Trying to connect by telnet...")
            client.connect("127.0.0.1", 3658)
            logger.info("Telnet connect successfully!")
            return@ArthasBridgeFactory ArthasBridgeImpl(TelnetArthasProcess(client))
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
        return searchJvm(jvm.context.template, jvm.context.providerConfig).result?.find { v -> v.id == jvm.id } == null
    }

    override fun tryCreateDefaultConfiguration(hostMachine: HostMachine): JvmProviderConfig {
        if (hostMachine !is ShellAvailableHostMachine) {
            return LocalJvmProviderConfig(false)
        }
        hostMachine.env("JAVA_HOME") ?.let {
            if (hostMachine.isDirectoryExist(it)) {
                return LocalJvmProviderConfig(true, it)
            }
        }
        hostMachine.grep("java.home", "java", "-XshowSettings:properties", "--version", "2>&1").tryUnwrap() ?.let {
            val i = it.indexOf('=')
            if (i >= 0) {
                return LocalJvmProviderConfig(true, it.substring(i + 1).trim())
            }
        }
        return LocalJvmProviderConfig(false)
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }


    override fun isSupport(hostMachine: HostMachine): Boolean {
        return hostMachine is ShellAvailableHostMachine
    }


}