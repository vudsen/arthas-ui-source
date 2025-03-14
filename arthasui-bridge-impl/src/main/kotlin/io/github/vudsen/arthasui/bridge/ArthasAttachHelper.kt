package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.components.Service
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.api.ArthasProcess
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.util.InteractiveShell2ArthasProcessAdapter
import io.github.vudsen.arthasui.bridge.util.ok
import org.apache.commons.net.telnet.TelnetClient
import java.io.InputStream
import java.io.OutputStream

/**
 * 协助 attach 操作
 */
@Service(Service.Level.APP)
class ArthasAttachHelper {


    companion object {
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

    /**
     * 连接本地的 jvm。 Windows 上必须使用 Telnet 连接，否则无法输入和输出
     */
    private fun createLocalFactory(hostMachine: HostMachine, jvm: JVM, localJvmProviderConfig: LocalJvmProviderConfig): ArthasBridgeFactory {
        if (hostMachine.getOS() != OS.WINDOWS) {
            return ArthasBridgeFactory {
                ArthasBridgeImpl(
                    InteractiveShell2ArthasProcessAdapter(hostMachine.createInteractiveShell(
                        "${localJvmProviderConfig.jdkHome}/bin/java",
                        "-jar",
                        "${localJvmProviderConfig.arthasHome}/arthas-boot.jar",
                        jvm.getId()
                    ))
                )
            }
        }

        return ArthasBridgeFactory {
            val result = hostMachine.execute(
                "${localJvmProviderConfig.jdkHome}/bin/java",
                "-jar",
                "${localJvmProviderConfig.arthasHome}/arthas-boot.jar",
                jvm.getId(),
                "--telnet-port",
                "3658",
                "--attach-only"
            )
            if (result.exitCode != 0) {
                TODO("Handle non-zero exit code.")
            }
            val client = TelnetClient().apply {
                connectTimeout = 10000
            }
            client.connect("127.0.0.1", 3658)
            return@ArthasBridgeFactory ArthasBridgeImpl(TelnetArthasProcess(client) as ArthasProcess)
        }
    }

    /**
     * 连接 docker 容器中的 jvm。除非开启 [JvmInDockerProviderConfig.useToolsInContainer]，否则不支持 docker desktop.
     */
    private fun createDockerFactory(hostMachine: HostMachine, jvm: JVM, config: JvmInDockerProviderConfig): ArthasBridgeFactory {
        if (hostMachine.getOS() == OS.WINDOWS && !config.useToolsInContainer) {
            throw IllegalStateException("Docker desktop is not supported. Please embed your jdk and arthas to your image and enable `useToolsInContainer` feature.")
        }
        return ArthasBridgeFactory {
            val jdkHome: String
            val arthasHome: String
            if (config.useToolsInContainer) {
                jdkHome = config.jdkHome
                arthasHome = config.arthasHome
            } else {
                hostMachine.execute("docker", "cp", config.arthasHome, "${jvm.getId()}:/tmp/arthas").ok()
                hostMachine.execute("docker", "cp", config.jdkHome, "${jvm.getId()}:/tmp/jdk").ok()
                jdkHome = "/tmp/jdk"
                arthasHome = "/tmp/arthas"
            }
            return@ArthasBridgeFactory ArthasBridgeImpl(InteractiveShell2ArthasProcessAdapter(
                // TODO, support switch pid.
                hostMachine.createInteractiveShell("docker", "exec", "-it", jvm.getId(), "$jdkHome/bin/java", "-jar", "${arthasHome}/arthas-boot.jar", "1"),
            ))
        }
    }

    fun createArthasBridgeFactory(hostMachine: HostMachine, jvm: JVM, jvmProviderConfig: JvmProviderConfig): ArthasBridgeFactory {
        return when(jvmProviderConfig::class) {
            LocalJvmProviderConfig::class -> createLocalFactory(hostMachine, jvm, jvmProviderConfig as LocalJvmProviderConfig)
            JvmInDockerProviderConfig::class -> createDockerFactory(hostMachine, jvm, jvmProviderConfig as JvmInDockerProviderConfig)
            else -> throw IllegalStateException("Unreachable code.")
        }
    }


}