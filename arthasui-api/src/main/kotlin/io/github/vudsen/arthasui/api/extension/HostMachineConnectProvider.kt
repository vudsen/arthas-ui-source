package io.github.vudsen.arthasui.api.extension

import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.FormComponent

interface HostMachineConnectProvider {

    /**
     * 获取名称
     */
    fun getName(): String

    /**
     * 创建一个表单, 用于创建一个对应的连接配置
     * @param oldEntity 旧配置，如果非空，表示为更新
     */
    fun createForm(oldEntity: HostMachineConnectConfig?): FormComponent<HostMachineConnectConfig>

    /**
     * 使用配置连接宿主机.
     * 如果连接需要关闭，则应该返回 [CloseableHostMachine] 的实例
     */
    fun connect(config: HostMachineConnectConfig): HostMachine

    /**
     * 获取配置文件的类
     */
    fun getConfigClass(): Class<out HostMachineConnectConfig>

    /**
     * 宿主机是否可以被关闭. 当该值返回 true 时，[getConfigClass] 应该返回 [CloseableHostMachine]
     */
    fun isCloseableHostMachine(): Boolean

}