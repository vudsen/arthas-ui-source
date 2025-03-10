package io.github.vudsen.arthasui.bridge.conf

import com.fasterxml.jackson.annotation.JsonIgnore
import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.currentOS
import javax.swing.Icon

class LocalConnectConfig : HostMachineConnectConfig(TYPE) {

    val os = currentOS()

    companion object {
        const val TYPE = "LocalMachineConfig"
    }

    @JsonIgnore
    override fun getIcon(): Icon {
        return AllIcons.Nodes.HomeFolder
    }

    override fun getOS(): OS {
        return os
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalConnectConfig

        return os == other.os
    }

    override fun hashCode(): Int {
        return os.hashCode()
    }


}

