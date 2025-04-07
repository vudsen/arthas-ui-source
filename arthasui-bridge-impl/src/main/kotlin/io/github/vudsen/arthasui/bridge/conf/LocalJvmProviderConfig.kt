package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class LocalJvmProviderConfig(
    enabled: Boolean = false,
    var arthasHome: String = "",
    var javaHome: String = ""
) : JvmProviderConfig(TYPE, enabled) {

    companion object {
        const val TYPE = "LocalJvmProviderConfig"
    }

    override fun getName(): String {
        return "Local JVM"
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }

    override fun copy(): JvmProviderConfig {
        return LocalJvmProviderConfig(enabled, arthasHome, javaHome)
    }

    override fun deepCopy(): JvmProviderConfig {
        return LocalJvmProviderConfig(enabled, arthasHome, javaHome)
    }

}