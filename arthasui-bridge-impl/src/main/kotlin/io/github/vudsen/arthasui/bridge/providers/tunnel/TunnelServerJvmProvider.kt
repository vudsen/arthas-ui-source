package io.github.vudsen.arthasui.bridge.providers.tunnel

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.bean.TunnelServerJvm
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import io.github.vudsen.arthasui.bridge.conf.TunnelServerProviderConfig
import io.github.vudsen.arthasui.bridge.host.TunnelServerHostMachine
import io.github.vudsen.arthasui.bridge.ui.TunnelServerProviderForm
import javax.swing.Icon

class TunnelServerJvmProvider : JvmProvider {

    override fun getName(): String {
        return "Tunnel Server"
    }

    override fun searchJvm(
        hostMachine: HostMachine,
        providerConfig: JvmProviderConfig
    ): JvmSearchResult {
        hostMachine as TunnelServerHostMachine
        return JvmSearchResult(null, hostMachine.listApps().map { appName -> TunnelServerAppChildSearcher(appName, hostMachine, providerConfig) })
    }


    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        return TunnelServerArthasBridgeFactory(jvm.context.template.getHostMachineConfig().connect as TunnelServerConnectConfig, jvm as TunnelServerJvm)
    }

    override fun createForm(
        oldState: JvmProviderConfig?,
        parentDisposable: Disposable
    ): FormComponent<JvmProviderConfig> {
        return TunnelServerProviderForm(oldState, parentDisposable)
    }

    override fun getConfigClass(): Class<out JvmProviderConfig> {
        return TunnelServerProviderConfig::class.java
    }

    override fun getJvmClass(): Class<out JVM> {
        return TunnelServerJvm::class.java
    }

    override fun isJvmInactive(jvm: JVM): Boolean {
        if (jvm !is TunnelServerJvm) {
            return true
        }
        val hostMachine = jvm.context.template as TunnelServerHostMachine
        val appName = jvm.id.substringBefore('_')
        if (appName.isEmpty()) {
            return true
        }
        return hostMachine.listAgents(appName).find { agent -> agent.agentId == jvm.id } == null
    }

    override fun tryCreateDefaultConfiguration(hostMachine: HostMachine): JvmProviderConfig {
        return TunnelServerProviderConfig(false)
    }

    override fun getIcon(): Icon {
        return AllIcons.Ide.Gift
    }

    override fun isHideCurrent(): Boolean {
        return true
    }

}