package io.github.vudsen.arthasui.bridge.conf

import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import javax.swing.Icon

class TunnelServerProviderConfig(enabled: Boolean = false) : JvmProviderConfig(TYPE, enabled) {

    companion object {
        const val TYPE = "Tunnel Server"
    }

    override fun getName(): String {
        return "Tunnel Server"
    }

    override fun getIcon(): Icon {
        return AllIcons.Ide.Gift
    }

    @Deprecated("Use deepCopy instead", replaceWith = ReplaceWith("deepCopy"))
    override fun copy(): JvmProviderConfig {
        return deepCopy()
    }

    override fun deepCopy(): JvmProviderConfig {
        return TunnelServerProviderConfig(enabled)
    }
}