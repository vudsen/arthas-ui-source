package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig

class CloseableHostMachineFallback(private val config: HostMachineConfig) : CloseableHostMachine {

    override fun isClosed(): Boolean {
        return true
    }

    override fun close() {}

    override fun getOS(): OS {
        return config.connect.getOS()
    }

    override fun getConfiguration(): HostMachineConnectConfig {
        return config.connect
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return config
    }

    override fun test() {}

    override fun <T : Any?> getUserData(p0: Key<T?>): T? {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> putUserData(p0: Key<T?>, p1: T?) {
        TODO("Not yet implemented")
    }
}