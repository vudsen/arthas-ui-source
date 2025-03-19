package io.github.vudsen.arthasui.bridge.conf

import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import javax.swing.Icon

class JvmInDockerProviderConfig(
    var enabled: Boolean = false,
    var dockerPath: String = "docker",
    /**
     * 使用容器自带的工具
     */
    var useToolsInContainer: Boolean = false,
    /**
     * jdk 目录
     */
    var jdkHome: String = "",
    /**
     * arthas 目录
     */
    var arthasHome: String = ""
) : JvmProviderConfig(TYPE){

    companion object {
        const val TYPE = "JvmInDockerProviderConfig"
    }

    override fun getName(): String {
        return "Docker"
    }

    override fun getIcon(): Icon {
        return AllIcons.Nodes.Console
    }

    override fun copy(): JvmProviderConfig {
        return JvmInDockerProviderConfig(enabled, dockerPath, useToolsInContainer, jdkHome, arthasHome)
    }
}