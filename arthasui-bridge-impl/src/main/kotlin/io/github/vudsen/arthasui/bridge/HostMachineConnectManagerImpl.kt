package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.bridge.providers.LocalHostMachineConnectProvider
import io.github.vudsen.arthasui.bridge.providers.SshHostMachineConnectProvider
import io.github.vudsen.arthasui.bridge.template.HostMachineTemplateFactory
import io.github.vudsen.arthasui.bridge.util.ReusableHostMachine
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
    }

    /**
     * 注册一个 provider.
     */
    override fun register(provider: HostMachineConnectProvider) {
        providers.putIfAbsent(provider.getConfigClass(), provider)
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
    override fun connect(config: HostMachineConfig): HostMachineTemplate {
        logger.info("Connecting to $config")
        val provider = getProvider(config.connect)
        val hostMachine: HostMachine = if (provider.isCloseableHostMachine()) {
            wrapCloseableHostMachine(provider, config.connect)
        } else {
            provider.connect(config.connect)
        }
        return HostMachineTemplateFactory.getHostMachineTemplate(config, hostMachine)
    }

    private fun wrapCloseableHostMachine(provider: HostMachineConnectProvider, config: HostMachineConnectConfig): CloseableHostMachine {
        cache[config] ?.let {
            logger.info("Return cached instance for connect config ${config}.")
            return it
        }
        synchronized(config) {
            cache[config] ?.let { return it }
            val instance = ReusableHostMachine(provider, config)
            logger.info("Created a new instance for connect config ${config}.")
            cache[config] = instance
            return instance
        }
    }

}