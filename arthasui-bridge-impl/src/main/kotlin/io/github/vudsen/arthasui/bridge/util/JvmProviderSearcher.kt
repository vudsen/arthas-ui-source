package io.github.vudsen.arthasui.bridge.util

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import javax.swing.Icon

/**
 * 将一个顶层的 [JvmProvider] 转换为 [JvmSearchDelegate]
 */
class JvmProviderSearcher(
    private val provider: JvmProvider,
    private val providerConfig: JvmProviderConfig,
    private val hostMachine: HostMachine
) : JvmSearchDelegate {

    override fun getName(): String {
        return provider.getName()
    }

    override fun getIcon(): Icon {
        return provider.getIcon()
    }

    override fun load(): JvmSearchResult {
        return provider.searchJvm(hostMachine, providerConfig)
    }


    override fun equals(other: Any?): Boolean {
        if (other == null || other !is JvmProviderSearcher) {
            return false
        }
        return providerConfig == other.providerConfig
    }

    override fun hashCode(): Int {
        return providerConfig.hashCode()
    }
}