package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class LocalJvmProviderConfig(
    enabled: Boolean = false,
    var javaHome: String = ""
) : JvmProviderConfig(TYPE, enabled) {

    companion object {
        const val TYPE = "LocalJvmProviderConfig"
    }

    override fun getName(): String {
        return "Local JVM"
    }

    @Deprecated("Use deepCopy instead", replaceWith = ReplaceWith("deepCopy"))
    override fun copy(): JvmProviderConfig {
        return LocalJvmProviderConfig(enabled, javaHome)
    }

    override fun deepCopy(): JvmProviderConfig {
        return LocalJvmProviderConfig(enabled, javaHome)
    }

}