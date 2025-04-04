package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon


class LocalJVM(pid: String, mainClass: String, context: JvmContext) : JVM(pid, mainClass, context) {

    override fun getIcon(): Icon {
        return ArthasUIIcons.Local
    }

    override fun toString(): String {
        return "type = local, $name"
    }

}