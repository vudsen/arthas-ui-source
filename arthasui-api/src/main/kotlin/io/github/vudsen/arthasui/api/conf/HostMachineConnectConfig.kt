package io.github.vudsen.arthasui.api.conf

import io.github.vudsen.arthasui.api.DeepCopyable
import io.github.vudsen.arthasui.api.OS
import javax.swing.Icon

abstract class HostMachineConnectConfig(
    val type: String,
) : DeepCopyable<HostMachineConnectConfig> {

    /**
     * 获取对应连接方式的图标
     */
    abstract fun getIcon(): Icon

    /**
     * 获取操作系统类型
     */
    abstract fun getOS(): OS


    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int
}