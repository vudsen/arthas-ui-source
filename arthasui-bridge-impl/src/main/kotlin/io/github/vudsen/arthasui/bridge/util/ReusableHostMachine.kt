package io.github.vudsen.arthasui.bridge.util

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager.Companion.ManagedInstance
import java.lang.ref.WeakReference

/**
 * 可以复用的宿主机，在连接关闭后再次使用，会重新创建一个连接
 */
class ReusableHostMachine(private val closeableHostMachineDelegate: HostMachineConnectProvider, private val config: HostMachineConnectConfig) : CloseableHostMachine {

    private var reference: WeakReference<ManagedInstance?> = WeakReference(null)


    override fun isClosed(): Boolean {
        reference.get() ?. let {
            return it.hostMachine.isClosed()
        } ?: return true
    }

    override fun close() {
        reference.get()?.hostMachine?.close()
    }

    private fun getInstance(): ManagedInstance {
        reference.get() ?.let {
            if (!it.hostMachine.isClosed()) {
                return it
            }
        }
        synchronized(ReusableHostMachine::class.java) {
            reference.get() ?.let {
                if (!it.hostMachine.isClosed()) {
                    return it
                }
            }
            val connectionManager = service<HostMachineConnectionManager>()
            val instance = closeableHostMachineDelegate.connect(config) as CloseableHostMachine
            val managedInstance = connectionManager.register(instance)
            this.reference = WeakReference(managedInstance)
            return managedInstance
        }
    }

    private fun getHostMachine(): HostMachine {
        for (spin in 0..100) {
            val managedInstance = getInstance()
            val connectionManager = service<HostMachineConnectionManager>()
            // 如果没有成功, 说明可能是刚拿到之前的连接后马上就被干掉了.
            if (connectionManager.resetTimeout(managedInstance)) {
                return managedInstance.hostMachine
            }
        }
        throw IllegalStateException("Failed to get HostMachine, spin has reach the maximum times.")
    }

    override fun execute(vararg command: String): CommandExecuteResult {
        return getHostMachine().execute(*command)
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        return getHostMachine().createInteractiveShell(*command)
    }

    override fun getOS(): OS {
        return config.getOS()
    }

}