package io.github.vudsen.arthasui.api

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import java.lang.ref.WeakReference


/**
 * 宿主机
 */
interface HostMachine : UserDataHolder{


    companion object {
        /**
         * 进度指示器. 实现类就可以通过该对象反馈进度.
         */
        val PROGRESS_INDICATOR = Key<WeakReference<ProgressIndicator>>("Download Indicator")
    }

    /**
     * 获取操作系统类型
     */
    fun getOS(): OS

    /**
     * 获取连接配置
     */
    @Deprecated("use getHostMachineConfig() instead", replaceWith = ReplaceWith("getHostMachineConfig().connect"))
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