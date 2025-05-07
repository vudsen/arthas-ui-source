package io.github.vudsen.arthasui.api.conf

import io.github.vudsen.arthasui.api.DeepCopyable
import javax.swing.Icon

abstract class JvmProviderConfig(val type: String, var enabled: Boolean) : DeepCopyable<JvmProviderConfig> {

    abstract fun getName(): String


    @Deprecated("Use deepCopy instead", replaceWith = ReplaceWith("deepCopy"))
    abstract fun copy(): JvmProviderConfig
}
