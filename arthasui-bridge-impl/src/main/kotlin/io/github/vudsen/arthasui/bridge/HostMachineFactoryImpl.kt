package io.github.vudsen.arthasui.bridge

import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.HostMachineFactory
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import java.util.*

class HostMachineFactoryImpl : HostMachineFactory {

    /**
     * 由客户端关闭
     */
    private val cache = WeakHashMap<HostMachineConnectConfig, CloseableHostMachine>()

    override fun getHostMachine(connectConfig: HostMachineConnectConfig): HostMachine {
        cache[connectConfig] ?.let {
            if (it.isClosed()) {
                cache.remove(connectConfig)
            } else {
                return it as HostMachine
            }
        }

        val instance = when (connectConfig::class) {
            LocalConnectConfig::class -> LocalHostMachineImpl()
            SshHostMachineConnectConfig::class -> RemoteSshHostMachineImpl(connectConfig as SshHostMachineConnectConfig)
            else -> throw IllegalStateException("Unknown hostMachineConfig type: ${connectConfig::class}")
        }
        if (instance is CloseableHostMachine) {
            cache[connectConfig] = instance
        }
        return instance
    }

}