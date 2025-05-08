package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.bridge.host.TunnelServerHostMachine
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class TunnelServerJvm(
    id: String,
    name: String,
    context: JvmContext,
    var agent: TunnelServerHostMachine.Companion.Agent
) : JVM(id, name, context) {

    override fun getIcon(): Icon {
        return ArthasUIIcons.Local
    }
}