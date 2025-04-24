package io.github.vudsen.arthasui

import ai.grazie.utils.WeakHashMap
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.currentOS
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.bridge.bean.SshConfiguration
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object BridgeTestUtil {


    fun createLocalHostMachine(): HostMachineTemplate {
        val config = HostMachineConfig(
            -1,
            "Test Local",
            false,
            LocalConnectConfig(),
            mutableListOf(LocalJvmProviderConfig()),
            mutableListOf(),
        )
        val template = service<HostMachineConnectManager>().connect(config)
        config.dataDirectory = template.generateDefaultDataDirectory()
        return template
    }

    fun createSshHostMachine(parentDisposable: Disposable, customise: ((GenericContainer<*>) -> Unit)?): HostMachineTemplate {
        val server = setupSshServer(parentDisposable, customise)
        val config = HostMachineConfig(
            -1,
            "Test Local",
            false,
            SshHostMachineConnectConfig(
                "Test server",
                SshConfiguration(server.host, server.firstMappedPort, "root", "root")
            ),
            mutableListOf(LocalJvmProviderConfig()),
            mutableListOf(),
        )
        val template = service<HostMachineConnectManager>().connect(config)
        config.dataDirectory = template.generateDefaultDataDirectory()
        return template
    }

    fun createSshHostMachine(parentDisposable: Disposable): HostMachineTemplate {
        return createSshHostMachine(parentDisposable, null)
    }

    private val instance = WeakHashMap<Disposable, GenericContainer<*>>()

    /**
     * 创建 SSH 服务器
     */
    private fun setupSshServer(rootDisposable: Disposable, customise: ((GenericContainer<*>) -> Unit)?): GenericContainer<*> {
        instance[rootDisposable] ?.let {
            return it
        }
        if (currentOS() != OS.LINUX) {
            throw UnsupportedOperationException("Only linux is allowed")
        }
        val sshContainer = GenericContainer(DockerImageName.parse("rastasheep/ubuntu-sshd:18.04"))
            .withExposedPorts(22)

        customise ?.let { it(sshContainer)  }

        Disposer.register(rootDisposable) {
            sshContainer.stop()
        }

        sshContainer.start();
        instance[rootDisposable] = sshContainer
        return sshContainer
    }

}