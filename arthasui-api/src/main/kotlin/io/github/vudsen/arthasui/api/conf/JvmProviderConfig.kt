package io.github.vudsen.arthasui.api.conf

import javax.swing.Icon

abstract class JvmProviderConfig(val type: String, var enabled: Boolean) {

    abstract fun getName(): String

    abstract fun getIcon(): Icon

    abstract fun copy(): JvmProviderConfig
}
