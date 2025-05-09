package io.github.vudsen.arthasui.bridge

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.bridge.providers.DockerJvmProvider
import io.github.vudsen.arthasui.bridge.providers.LocalJvmProvider
import io.github.vudsen.arthasui.bridge.providers.tunnel.TunnelServerJvmProvider

class JvmProviderManagerImpl : JvmProviderManager {

    private val providers = mutableMapOf<Class<out JvmProviderConfig>, JvmProvider>()

    init {
        register(LocalJvmProvider())
        register(DockerJvmProvider())
        register(TunnelServerJvmProvider())
    }


    override fun register(provider: JvmProvider) {
        providers[provider.getConfigClass()] ?.let {
            throw IllegalArgumentException("Config class ${provider.javaClass} with config class ${provider.getConfigClass()} has already registered")
        }
        providers[provider.getConfigClass()] = provider
    }

    override fun getProviders(): List<JvmProvider> {
        return providers.values.toList()
    }

    override fun getProvider(providerConfig: JvmProviderConfig): JvmProvider {
        return getProvider(providerConfig::class.java)
    }

    override fun getProvider(clazz: Class<out JvmProviderConfig>): JvmProvider {
        return providers[clazz] ?: throw IllegalArgumentException("No provider registered for config $clazz")
    }

    override fun findProviderByJvm(providerConfigs: List<JvmProviderConfig>, jvm: JVM): JvmProviderConfig? {
        for (config in providerConfigs) {
            if (getProvider(config).getJvmClass() == jvm::class.java) {
                return config
            }
        }
        return null
    }

}