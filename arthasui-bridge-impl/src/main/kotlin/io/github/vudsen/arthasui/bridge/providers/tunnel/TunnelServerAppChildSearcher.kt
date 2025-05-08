package io.github.vudsen.arthasui.bridge.providers.tunnel

import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.bridge.bean.TunnelServerJvm
import io.github.vudsen.arthasui.bridge.host.TunnelServerHostMachine
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

/**
 * Tunnel Server App Name 的一层，可以根据 App Name 再列出下面的 Agent Id
 */
class TunnelServerAppChildSearcher(
    private val appName: String,
    private val hostMachine: TunnelServerHostMachine,
    private val providerConfig: JvmProviderConfig
) : JvmSearchDelegate {

    override fun getName(): String {
        return appName
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }

    override fun load(): JvmSearchResult {
        val ctx = JvmContext(hostMachine, providerConfig)
        return JvmSearchResult(
            hostMachine.listAgents(appName).map { agent -> TunnelServerJvm(agent.agentId, agent.agentId.substringBefore('_'), ctx, agent) }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is TunnelServerAppChildSearcher) {
            return false
        }
        return appName == other.appName
    }

    override fun hashCode(): Int {
        return appName.hashCode()
    }

}