package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon


class DockerJvm(containerId: String, displayName: String, context: JvmContext) : JVM(containerId, displayName, context) {

    override fun getIcon(): Icon {
        return ArthasUIIcons.Docker
    }


    override fun toString(): String {
        return "type: docker, $name"
    }

}