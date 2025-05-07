package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.host.SshLinuxHostMachineImpl
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.ui.SshConfigurationForm
import org.apache.sshd.common.Factory
import org.apache.sshd.common.util.threads.CloseableExecutorService
import org.apache.sshd.common.util.threads.SshThreadPoolExecutor
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class SshHostMachineConnectProvider : HostMachineConnectProvider, Disposable {

    private val executor = SshThreadPoolExecutor(1, 4, 1L, TimeUnit.MINUTES, ArrayBlockingQueue(16))

    inner class MyCloseableExecutorService : Factory<CloseableExecutorService> {
        override fun create(): CloseableExecutorService? {
            return executor
        }
    }

    override fun getName(): String {
        return "SSH"
    }

    override fun createForm(oldEntity: HostMachineConnectConfig?, parentDisposable: Disposable): FormComponent<HostMachineConnectConfig> {
        return SshConfigurationForm(oldEntity, parentDisposable)
    }

    override fun connect(config: HostMachineConfig): CloseableHostMachine {
        return SshLinuxHostMachineImpl(config, MyCloseableExecutorService())
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        return SshHostMachineConnectConfig::class.java
    }

    override fun isCloseableHostMachine(): Boolean {
        return true
    }

    override fun dispose() {
        executor.shutdown()
    }

}