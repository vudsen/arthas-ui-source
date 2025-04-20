package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class JvmInDockerProviderConfig(
    enabled: Boolean = false,
    var dockerPath: String = "docker",
    /**
     * 容器中的 java 目录
     */
    var javaHome: String = "",
) : JvmProviderConfig(TYPE, enabled){

    companion object {
        const val TYPE = "JvmInDockerProviderConfig"
    }

    override fun getName(): String {
        return "Docker"
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }

    @Deprecated("Use deepCopy instead", replaceWith = ReplaceWith("deepCopy"))
    override fun copy(): JvmProviderConfig {
        return JvmInDockerProviderConfig(enabled, dockerPath, javaHome)
    }

    override fun deepCopy(): JvmProviderConfig {
        return JvmInDockerProviderConfig(enabled, dockerPath, javaHome)
    }
}