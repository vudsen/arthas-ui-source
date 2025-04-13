package io.github.vudsen.arthasui.api.bean

import com.intellij.icons.AllIcons
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import javax.swing.Icon

class EmptyConnectConfig : HostMachineConnectConfig(TYPE, ""){

    companion object {
        const val TYPE = "Empty"
    }

    override fun getIcon(): Icon {
        return AllIcons.Nodes.Console
    }

    override fun getOS(): OS {
        return OS.LINUX
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun deepCopy(): HostMachineConnectConfig {
        return EmptyConnectConfig()
    }


}