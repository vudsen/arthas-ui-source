package io.github.vudsen.arthasui.bridge.conf

import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import javax.swing.Icon

class LocalJvmProviderConfig(
    var enabled: Boolean = false,
    var arthasHome: String = "",
    var jdkHome: String = ""
) : JvmProviderConfig(TYPE) {

    companion object {
        const val TYPE = "LocalJvmProviderConfig"
    }

    override fun getName(): String {
        return "Local JVM"
    }

    override fun getIcon(): Icon {
        return AllIcons.Nodes.Console
    }

    override fun copy(): JvmProviderConfig {
        return LocalJvmProviderConfig(enabled, arthasHome, jdkHome)
    }

}