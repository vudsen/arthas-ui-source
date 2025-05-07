package io.github.vudsen.arthasui.api.extension

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig

interface HostMachineConnectManager : Disposable {

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
    fun connect(config: HostMachineConfig): HostMachine

    /**
     * 根据配置获取 provider
     */
    fun getProvider(config: HostMachineConnectConfig): HostMachineConnectProvider

}