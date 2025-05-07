package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import javax.swing.Icon

class TunnelServerJvm(
    id: String,
    name: String,
    context: JvmContext
) : JVM(id, name, context) {

    override fun getIcon(): Icon {
        TODO("Not yet implemented")
    }
}