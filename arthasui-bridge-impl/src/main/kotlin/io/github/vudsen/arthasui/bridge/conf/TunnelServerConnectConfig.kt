package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import javax.swing.Icon

class TunnelServerConnectConfig(
    var baseUrl: String = "http://localhost:8080",
    var wsPath: String = "ws://127.0.0.1:7777/ws"
) : HostMachineConnectConfig(TYPE) {

    override fun getIcon(): Icon {
        TODO("Not yet implemented")
    }

    override fun getOS(): OS {
        return OS.UNKNOWN
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }

    override fun deepCopy(): HostMachineConnectConfig {
        TODO("Not yet implemented")
    }

    companion object {
        const val TYPE = "TunnelServer"
    }

}