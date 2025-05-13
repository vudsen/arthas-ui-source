package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.bridge.host.AbstractLinuxShellAvailableHostMachine
import io.github.vudsen.arthasui.bridge.host.CloseableHostMachineFallback
import io.github.vudsen.arthasui.bridge.providers.LocalHostMachineConnectProvider
import io.github.vudsen.arthasui.bridge.providers.SshHostMachineConnectProvider
import io.github.vudsen.arthasui.bridge.providers.k8s.K8sConnectProvider
import io.github.vudsen.arthasui.bridge.providers.tunnel.TunnelServerConnectProvider
import io.github.vudsen.arthasui.bridge.util.PooledResource
import java.lang.reflect.Proxy
import java.util.*

/**
 * 注册 + 工厂
 */
class HostMachineConnectManagerImpl : HostMachineConnectManager {

    companion object {
        val logger = Logger.getInstance(HostMachineConnectManagerImpl::class.java)
    }

    private val providers = mutableMapOf<Class<out HostMachineConnectConfig>, HostMachineConnectProvider>()

    private val cache = WeakHashMap<HostMachineConnectConfig, CloseableHostMachine>()

    init {
        register(LocalHostMachineConnectProvider())
        register(SshHostMachineConnectProvider())
        register(TunnelServerConnectProvider())
        register(K8sConnectProvider())
    }

    /**
     * 注册一个 provider.
     */
    override fun register(provider: HostMachineConnectProvider) {
        providers.putIfAbsent(provider.getConfigClass(), provider)
        if (provider is Disposable) {
            Disposer.register(this, provider)
        }
    }

    /**
     * 获取所有 provider
     */
    override fun getProviders(): List<HostMachineConnectProvider> {
        return providers.values.toList()
    }

    /**
     * 获取对应的 provider
     */
    override fun getProvider(config: HostMachineConnectConfig): HostMachineConnectProvider {
        return providers[config::class.java] ?: throw IllegalArgumentException("No provider registered for config $config")
    }

    /**
     * 工具方法，快速连接某个宿主机
     */
    override fun connect(config: HostMachineConfig): HostMachine {
        logger.info("Connecting to $config")
        val provider = getProvider(config.connect)

        val connect = provider.connect(config)
        if (connect is CloseableHostMachine) {
            return wrapCloseableHostMachine(connect, provider, config)
        }
        return connect
    }

    private fun wrapCloseableHostMachine(hostMachine: CloseableHostMachine, provider: HostMachineConnectProvider, config: HostMachineConfig): CloseableHostMachine {
        cache[config.connect] ?.let {
            logger.info("Return cached instance for connect config ${config}.")
            return it
        }

        synchronized(config) {
            cache[config.connect] ?.let { return it }
            val interfaces = if (hostMachine is AbstractLinuxShellAvailableHostMachine) {
                arrayOf(*hostMachine::class.java.interfaces, ShellAvailableHostMachine::class.java)
            } else {
                hostMachine::class.java.interfaces
            }
            val instance = Proxy.newProxyInstance(
                HostMachineConnectManagerImpl::class.java.classLoader,
                interfaces,
                PooledResource<CloseableHostMachine>(hostMachine, CloseableHostMachineFallback(config)) {
                    return@PooledResource provider.connect(config) as CloseableHostMachine
                }
            ) as CloseableHostMachine
            logger.info("Created a new instance for connect config ${config}.")
            cache[config.connect] = instance
            return instance
        }
    }

    override fun dispose() {
        for (entry in cache) {
            entry.value.close()
        }
    }

}