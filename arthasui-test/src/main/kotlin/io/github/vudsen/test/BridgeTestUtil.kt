package io.github.vudsen.test

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
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

    fun createLocalHostMachine(): ShellAvailableHostMachine {
        val config = HostMachineConfig(
            (Math.random() * 10000).toLong(),
            "Test Local",
            LocalConnectConfig(),
            mutableListOf(LocalJvmProviderConfig()),
            mutableListOf(),
        )
        val template = service<HostMachineConnectManager>().connect(config) as ShellAvailableHostMachine
        config.dataDirectory = template.resolveDefaultDataDirectory() + "/test"
        return template
    }

    fun createSshHostMachine(parentDisposable: Disposable, customise: (GenericContainer<*>.() -> Unit)?): ShellAvailableHostMachine {
        val server = setupContainer("rastasheep/ubuntu-sshd:18.04", parentDisposable) {
            customise?.invoke(this)
            withExposedPorts(22)
        }
        val config = HostMachineConfig(
            -1,
            "Remote",
            SshHostMachineConnectConfig(
                "Test server",
                SshConfiguration(server.host, server.firstMappedPort, "root", "root")
            ),
            mutableListOf(LocalJvmProviderConfig()),
            mutableListOf(),
        )
        val template = service<HostMachineConnectManager>().connect(config) as ShellAvailableHostMachine
        config.dataDirectory = template.resolveDefaultDataDirectory() + "/test"
        return template
    }

    fun createSshHostMachine(parentDisposable: Disposable): ShellAvailableHostMachine {
        return createSshHostMachine(parentDisposable, null)
    }

    fun createMathGameSshMachine(parentDisposable: Disposable): ShellAvailableHostMachine {
        val server = setupContainer("vudsen/ssh-server-with-math-game:0.0.3", parentDisposable) {
            withExposedPorts(22)
        }
        val config = HostMachineConfig(
            -1,
            "Remote",
            SshHostMachineConnectConfig(
                "Test server",
                SshConfiguration(server.host, server.firstMappedPort, "root", "root")
            ),
            mutableListOf(LocalJvmProviderConfig(true, "/opt/java")),
            mutableListOf(),
            "/opt/arthas-ui"
        )
        val template = service<HostMachineConnectManager>().connect(config) as ShellAvailableHostMachine
        return template
    }

    /**
     * 创建容器
     */
    fun setupContainer(image: String, rootDisposable: Disposable, customise: (GenericContainer<*>.() -> Unit)?): GenericContainer<*> {
        val sshContainer = GenericContainer(DockerImageName.parse(image))

        customise ?.let { it(sshContainer)  }

        Disposer.register(rootDisposable) {
            sshContainer.stop()
        }

        sshContainer.start();
        return sshContainer
    }

    /**
     * 包含一个 `test.txt` 的 tgz 文件
     */
    private val TGZ_BASE64 = Base64.decode("H4sIAADRCWgAA+3QMQrCQBAF0D3KnkBmN1nvY2EdSEbw+IaAjYVWsXqv+QO/+Uzet7zkM8uJYned5yN3n3ncbUT0aURvo0RrfUylxpmj3h5b3tZay7osX5/wqwcAAAAAAAAAAIA/egGxS8lZACgAAA==")

    /**
     * 包含一个 `test.txt` 的 zip 文件
     */
    private val ZIP_BASE64 = Base64.decode("UEsDBAoAAAAAAGVw01oAAAAAAAAAAAAAAAAIABwAdGVzdC50eHRVVAkAAx6oU2geqFNodXgLAAEEAAAAAAQAAAAAUEsBAh4DCgAAAAAAZXDTWgAAAAAAAAAAAAAAAAgAGAAAAAAAAAAAAKSBAAAAAHRlc3QudHh0VVQFAAMeqFNodXgLAAEEAAAAAAQAAAAAUEsFBgAAAAABAAEATgAAAEIAAAAAAA==")


    /**
     * 在本地创建一个 tar.gz 压缩包
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

    /**
     * 在本地创建一个 zip 压缩包
     * @param file 目标路径
     * @return 压缩包中的文件
     */
    fun createTestZipFile(file: File): List<String> {
        file.parentFile.mkdirs()
        FileOutputStream(file).use { fos ->
            fos.write(ZIP_BASE64)
        }
        return listOf("test.txt")
    }

}