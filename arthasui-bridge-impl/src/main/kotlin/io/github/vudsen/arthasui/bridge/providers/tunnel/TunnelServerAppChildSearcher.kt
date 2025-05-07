package io.github.vudsen.arthasui.bridge.providers.tunnel

import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.bridge.bean.TunnelServerJvm
import io.github.vudsen.arthasui.bridge.host.TunnelServerHostMachine
import javax.swing.Icon

class TunnelServerAppChildSearcher(
    private val appName: String,
    private val hostMachine: TunnelServerHostMachine,
    private val providerConfig: JvmProviderConfig
) : JvmSearchResult.Companion.ChildSearcher {

    override fun getName(): String {
        return appName
    }

    override fun getIcon(): Icon {
        return AllIcons.Ide.Gift
    }

    override fun load(): JvmSearchResult {
        val ctx = JvmContext(hostMachine, providerConfig)
        return JvmSearchResult(
            hostMachine.listAgents(appName).map { agentId -> TunnelServerJvm(agentId, agentId, ctx) }
        )
    }

}