package io.github.vudsen.arthasui.bridge.conf

import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class TunnelServerConnectConfig(
    var baseUrl: String = "http://localhost:8080",
    var wsPath: String = "ws://127.0.0.1:7777/ws"
) : HostMachineConnectConfig(TYPE) {

    override fun getIcon(): Icon {
        return ArthasUIIcons.Cloud
    }

    override fun getOS(): OS {
        return OS.UNKNOWN
    }

    override fun deepCopy(): HostMachineConnectConfig {
        return TunnelServerConnectConfig(baseUrl, wsPath)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TunnelServerConnectConfig

        if (baseUrl != other.baseUrl) return false
        if (wsPath != other.wsPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = baseUrl.hashCode()
        result = 31 * result + wsPath.hashCode()
        return result
    }

    companion object {
        const val TYPE = "TunnelServer"
    }

}