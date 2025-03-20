package io.github.vudsen.arthasui.api.extension

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig

interface JvmProviderManager {


    /**
     * 注册一个 provider.
     */
    fun register(provider: JvmProvider)

    /**
     * 获取所有的 provider
     */
    fun getProviders(): List<JvmProvider>

    /**
     * 获取对应的 provider
     */
    fun getProvider(providerConfig: JvmProviderConfig): JvmProvider

    /**
     * 根据 JVM 找到对应的 [JvmProviderConfig]
     */
    fun findProviderByJvm(providerConfigs: List<JvmProviderConfig>, jvm: JVM): JvmProviderConfig?

}