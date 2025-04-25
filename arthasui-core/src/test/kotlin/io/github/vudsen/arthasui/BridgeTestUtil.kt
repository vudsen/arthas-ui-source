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
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Base64
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.io.FileOutputStream

object BridgeTestUtil {

    fun createLocalHostMachine(): HostMachineTemplate {
        val config = HostMachineConfig(
            -1,
            "Test Local",
            null,
            LocalConnectConfig(),
            mutableListOf(LocalJvmProviderConfig()),
            mutableListOf(),
        )
        val template = service<HostMachineConnectManager>().connect(config)
        config.dataDirectory = template.resolveDefaultDataDirectory() + "/test"
        return template
    }

    fun createSshHostMachine(parentDisposable: Disposable, customise: ((GenericContainer<*>) -> Unit)?): HostMachineTemplate {
        val server = setupSshServer(parentDisposable, customise)
        val config = HostMachineConfig(
            -1,
            "Test Local",
            null,
            SshHostMachineConnectConfig(
                "Test server",
                SshConfiguration(server.host, server.firstMappedPort, "root", "root")
            ),
            mutableListOf(LocalJvmProviderConfig()),
            mutableListOf(),
        )
        val template = service<HostMachineConnectManager>().connect(config)
        config.dataDirectory = template.resolveDefaultDataDirectory() + "/test"
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

    /**
     * 包含一个 `test.txt` 的 tgz 文件
     */
    private val TGZ_BASE64 = Base64.decode("H4sIAADRCWgAA+3QMQrCQBAF0D3KnkBmN1nvY2EdSEbw+IaAjYVWsXqv+QO/+Uzet7zkM8uJYned5yN3n3ncbUT0aURvo0RrfUylxpmj3h5b3tZay7osX5/wqwcAAAAAAAAAAIA/egGxS8lZACgAAA==")



    /**
     * 在本地创建一个压缩包
     * @param file 目标路径
     * @return 压缩包中的文件
     */
    fun createTestTarGzFile(file: File): List<String> {
        file.parentFile.mkdirs()
        FileOutputStream(file).use { fos ->
            fos.write(TGZ_BASE64)
        }
        return listOf("test.txt")
    }

}