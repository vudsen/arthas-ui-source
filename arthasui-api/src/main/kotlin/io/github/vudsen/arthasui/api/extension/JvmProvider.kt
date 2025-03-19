package io.github.vudsen.arthasui.api.extension

import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.FormComponent

interface JvmProvider {

    /**
     * 获取名称
     */
    fun getName(): String

    /**
     * 搜索默认位置下的 jvm
     */
    fun searchJvm(hostMachine: HostMachine, providerConfig: JvmProviderConfig): List<JVM>

    /**
     * 创建一个 [ArthasBridgeFactory]
     */
    fun createArthasBridgeFactory(hostMachine: HostMachine, jvm: JVM, jvmProviderConfig: JvmProviderConfig): ArthasBridgeFactory

    /**
     * 创建一个表单
     * @param oldState 如果非空，表示更新
     */
    fun createForm(oldState: JvmProviderConfig?): FormComponent<JvmProviderConfig>

    /**
     * 获取配置的 class
     */
    fun getConfigClass(): Class<out JvmProviderConfig>

    /**
     * 获取搜索到的 JVM 类型
     */
    fun getJvmClass(): Class<out JVM>

}