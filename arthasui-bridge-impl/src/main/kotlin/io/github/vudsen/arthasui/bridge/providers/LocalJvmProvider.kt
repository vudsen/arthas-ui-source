package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.ui.LocalJvmProviderForm
import io.github.vudsen.arthasui.bridge.util.InteractiveShell2ArthasProcessAdapter
import io.github.vudsen.arthasui.bridge.util.BridgeUtils
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

    override fun searchJvm(hostMachine: HostMachine, providerConfig: JvmProviderConfig): List<JVM> {
        val config = providerConfig as LocalJvmProviderConfig
        return BridgeUtils.parseJpsOutput(hostMachine.execute("${config.jdkHome}/bin/jps", "-l").ok())
    }


    private fun createLocalFactory(hostMachine: HostMachine, jvm: JVM, localJvmProviderConfig: LocalJvmProviderConfig): ArthasBridgeFactory {
        if (hostMachine.getOS() != OS.WINDOWS) {
            return ArthasBridgeFactory {
                ArthasBridgeImpl(
                    InteractiveShell2ArthasProcessAdapter(hostMachine.createInteractiveShell(
                        "${localJvmProviderConfig.jdkHome}/bin/java",
                        "-jar",
                        "${localJvmProviderConfig.arthasHome}/arthas-boot.jar",
                        jvm.id
                    ))
                )
            }
        }

        return ArthasBridgeFactory {
            logger.info("Trying to attach ${jvm.id}, binding telnet port on 3658.")
            // TODO 自动切换端口
            val stdout = hostMachine.execute(
                "${localJvmProviderConfig.jdkHome}/bin/java",
                "-jar",
                "${localJvmProviderConfig.arthasHome}/arthas-boot.jar",
                jvm.id,
                "--telnet-port",
                "3658",
                "--attach-only"
            ).ok()

            if (logger.isDebugEnabled) {
                logger.info("Attach success!, stdout: $stdout")
            }
            val client = TelnetClient().apply {
                connectTimeout = 10000
            }
            logger.info("Trying to connect by telnet...")
            client.connect("127.0.0.1", 3658)
            logger.info("Telnet connect successfully!")
            return@ArthasBridgeFactory ArthasBridgeImpl(TelnetArthasProcess(client) as ArthasProcess)
        }
    }

    override fun createArthasBridgeFactory(
        hostMachine: HostMachine,
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        return createLocalFactory(hostMachine, jvm, jvmProviderConfig as LocalJvmProviderConfig)
    }

    override fun createForm(oldState: JvmProviderConfig?, parentDisposable: Disposable): FormComponent<JvmProviderConfig> {
        return LocalJvmProviderForm(oldState, parentDisposable)
    }

    override fun getConfigClass(): Class<out JvmProviderConfig> {
        return LocalJvmProviderConfig::class.java
    }

    override fun getJvmClass(): Class<out JVM> {
        return LocalJVM::class.java
    }

}