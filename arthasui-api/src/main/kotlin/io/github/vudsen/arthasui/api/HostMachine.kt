package io.github.vudsen.arthasui.api

import com.intellij.openapi.util.UserDataHolder
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig


/**
 * 宿主机。部分耗时的任务可以使用 [io.github.vudsen.arthasui.common.util.ProgressIndicatorStack] 来传递进度条
 */
interface HostMachine : UserDataHolder{

    /**
     * 获取操作系统类型
     */
    fun getOS(): OS

    /**
     * 获取连接配置
     */
    fun getConfiguration(): HostMachineConnectConfig

    /**
     * 获取配置
     */
    fun getHostMachineConfig(): HostMachineConfig


    /**
     * 测试连接
     */
    fun test()
}