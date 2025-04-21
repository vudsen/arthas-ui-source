package io.github.vudsen.arthasui.api.extension

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate

interface HostMachineConnectManager {

    /**
     * 注册一个 provider.
     */
    fun register(provider: HostMachineConnectProvider)

    /**
     * 获取所有 provider
     */
    fun getProviders(): List<HostMachineConnectProvider>

    /**
     * 工具方法，快速连接某个宿主机
     */
    fun connect(config: HostMachineConfig): HostMachineTemplate

    /**
     * 根据配置获取 provider
     */
    fun getProvider(config: HostMachineConnectConfig): HostMachineConnectProvider

}