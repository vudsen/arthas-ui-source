package io.github.vudsen.arthasui.api.conf

import javax.swing.Icon

abstract class JvmProviderConfig(val type: String) {

    abstract fun getName(): String

    abstract fun getIcon(): Icon

    abstract fun copy(): JvmProviderConfig
}
