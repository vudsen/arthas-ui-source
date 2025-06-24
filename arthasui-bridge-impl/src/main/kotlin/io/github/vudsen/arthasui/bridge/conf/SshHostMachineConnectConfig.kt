package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.bridge.bean.SshConfiguration
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class SshHostMachineConnectConfig(
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
    /**
     * 当该值非空时，表示优先使用本地的包进行传输，而不是让宿主机自己下载.
     *
     * 该值对应一个存在的 [HostMachineConfig.id]，并且 [HostMachineConfig.connect] 一定是 [io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig]
     */
    var localPkgSourceId: Long? = null,
) : HostMachineConnectConfig(TYPE) {

    companion object {
        const val TYPE = "SshHostMachineConfig"
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Server
    }

    override fun getOS(): OS {
        return os
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
        return SshHostMachineConnectConfig(name, ssh, os, localPkgSourceId)
    }

    override fun toString(): String {
        return "SSH(name = $name)"
    }

}