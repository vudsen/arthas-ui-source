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

/**
 * 可以复用的宿主机，在连接关闭后再次使用，会重新创建一个连接
 */
class ReusableHostMachine(private val closeableHostMachineDelegate: HostMachineConnectProvider, private val config: HostMachineConnectConfig) : CloseableHostMachine {

    private var instance: ManagedInstance? = null


    override fun isClosed(): Boolean {
        instance ?.let {
            return it.hostMachine.isClosed()
        } ?: return true
    }

    override fun close() {
        instance?.hostMachine?.close()
    }

    private fun getInstance(): ManagedInstance {
        instance ?.let {
            if (it.hostMachine.isClosed()) {
                instance = null
            } else {
                return it
            }
        }
        synchronized(ReusableHostMachine::class.java) {
            instance ?.let {
                if (it.hostMachine.isClosed()) {
                    instance = null
                } else {
                    return it
                }
            }
            val connectionManager = service<HostMachineConnectionManager>()
            val instance = closeableHostMachineDelegate.connect(config) as CloseableHostMachine
            val managedInstance = connectionManager.register(instance)
            this.instance = managedInstance
            return managedInstance
        }
    }

    private fun getHostMachine(): HostMachine {
        val managedInstance = getInstance()
        val connectionManager = service<HostMachineConnectionManager>()
        connectionManager.resetTimeout(managedInstance)
        return managedInstance.hostMachine
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