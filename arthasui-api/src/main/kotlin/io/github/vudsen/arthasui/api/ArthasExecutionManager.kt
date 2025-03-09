package io.github.vudsen.arthasui.api

import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.bean.VirtualFileAttributes

interface ArthasExecutionManager {

    companion object {
        /**
         * 在创建 [com.intellij.openapi.vfs.VirtualFile] 时，添加必要的参数以开启 console
         */
        val VF_ATTRIBUTES = Key.create<VirtualFileAttributes>("VirtualFileAttributes")
    }

    /**
     * 初始化一个 [ArthasBridgeTemplate]
     */
    fun initTemplate(jvm: JVM, hostMachineConfig: HostMachineConnectConfig, providerConfig: JvmProviderConfig): ArthasBridgeTemplate

    /**
     * 是否已经连接过了
     */
    fun isAttached(jvm: JVM): Boolean

    /**
     * 获取已经创建的 [ArthasBridgeTemplate]，需要先调用 [initTemplate] 来缓存
     */
    fun getTemplate(jvm: JVM): ArthasBridgeTemplate?


}