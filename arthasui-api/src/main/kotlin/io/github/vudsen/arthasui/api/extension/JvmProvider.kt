package io.github.vudsen.arthasui.api.extension

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.FormComponent
import javax.swing.Icon

interface JvmProvider {

    /**
     * 获取名称
     */
    fun getName(): String

    /**
     * 搜索默认位置下的 jvm
     */
    fun searchJvm(hostMachine: HostMachine, providerConfig: JvmProviderConfig): JvmSearchResult

    /**
     * 创建一个 [ArthasBridgeFactory]
     */
    fun createArthasBridgeFactory(jvm: JVM, jvmProviderConfig: JvmProviderConfig): ArthasBridgeFactory

    /**
     * 创建一个表单
     * @param oldState 如果非空，表示更新
     */
    fun createForm(oldState: JvmProviderConfig?, parentDisposable: Disposable): FormComponent<JvmProviderConfig>

    /**
     * 获取配置的 class
     */
    fun getConfigClass(): Class<out JvmProviderConfig>

    /**
     * 获取搜索到的 JVM 类型
     */
    fun getJvmClass(): Class<out JVM>

    /**
     * jvm 是否不存在
     */
    fun isJvmInactive(jvm: JVM): Boolean

    /**
     * 尝试在宿主机上生成对应的配置
     * @return 对应的 [JvmProviderConfig], 如果不支持, [JvmProviderConfig.enabled] 为 false
     */
    fun tryCreateDefaultConfiguration(hostMachine: HostMachine): JvmProviderConfig

    /**
     * 获取图标
     */
    fun getIcon(): Icon


    /**
     * 是否支持该宿主机
     */
    fun isSupport(hostMachine: HostMachine): Boolean

}