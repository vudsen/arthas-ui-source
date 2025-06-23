package io.github.vudsen.arthasui.api.extension

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.UIContext
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.FormComponent

interface HostMachineConnectProvider {

    /**
     * 获取名称
     */
    fun getName(): String


    /**
     * 创建表达
     */
    fun createForm(oldEntity: HostMachineConnectConfig?, parentDisposable: Disposable): FormComponent<HostMachineConnectConfig>


    /**
     * 使用配置连接宿主机.
     * 如果连接需要关闭，则应该返回 [CloseableHostMachine] 的实例
     */
    fun connect(config: HostMachineConfig): HostMachine

    /**
     * 获取配置文件的类
     */
    fun getConfigClass(): Class<out HostMachineConnectConfig>

    /**
     * 获取最终连接的类, 若返回值非空，表示该 Provider 创建的宿主机需要关闭
     */
    fun getConnectionClassForLazyLoad(): Class<out HostMachine>?

}