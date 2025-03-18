package io.github.vudsen.arthasui.bridge

import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider

class ReusableHostMachine(private val closeableHostMachineDelegate: HostMachineConnectProvider, private val config: HostMachineConnectConfig) : CloseableHostMachine {

    private var instance: CloseableHostMachine? = null


    override fun isClosed(): Boolean {
        instance ?.let {
            return it.isClosed()
        } ?: return true
    }

    override fun close() {
        instance?.close()
    }

    private fun getInstance(): CloseableHostMachine {
        instance ?.let {
            return it
        }
        synchronized(ReusableHostMachine::class.java) {
            instance ?.let {
                return it
            }
            val instance = closeableHostMachineDelegate.connect(config) as CloseableHostMachine
            this.instance = instance
            return instance
        }
    }

    override fun execute(vararg command: String): CommandExecuteResult {
        return getInstance().execute(*command)
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        return getInstance().createInteractiveShell(*command)
    }

    override fun getOS(): OS {
        return config.getOS()
    }

}