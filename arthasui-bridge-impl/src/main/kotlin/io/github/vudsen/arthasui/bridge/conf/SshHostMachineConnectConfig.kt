package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.bridge.bean.SshConfiguration
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class SshHostMachineConnectConfig(
    dataDirectory: String,
    /**
     * 远程主机名称
     */
    var name: String = "",
    /**
     * ssh 配置
     */
    var ssh: SshConfiguration = SshConfiguration(),
    /**
     * 操作系统
     */
    var os: OS = OS.LINUX,
) : HostMachineConnectConfig(TYPE, dataDirectory) {

    companion object {
        const val TYPE = "SshHostMachineConfig"
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Server
    }

    override fun getOS(): OS {
        return os
    }

    override fun isRequireClose(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SshHostMachineConnectConfig

        if (name != other.name) return false
        if (ssh != other.ssh) return false
        if (os != other.os) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + ssh.hashCode()
        result = 31 * result + os.hashCode()
        return result
    }

    override fun deepCopy(): HostMachineConnectConfig {
        return SshHostMachineConnectConfig(dataDirectory, name, ssh, os)
    }

    override fun toString(): String {
        return "SSH(name = $name)"
    }

}