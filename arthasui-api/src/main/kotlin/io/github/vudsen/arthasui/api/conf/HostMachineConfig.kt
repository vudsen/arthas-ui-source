package io.github.vudsen.arthasui.api.conf

import com.intellij.util.xmlb.annotations.OptionTag
import io.github.vudsen.arthasui.api.DeepCopyable
import io.github.vudsen.arthasui.api.bean.EmptyConnectConfig
import io.github.vudsen.arthasui.api.bean.JvmSearchGroup
import io.github.vudsen.arthasui.api.util.mapMutable

data class HostMachineConfig(
    /**
     * 唯一 id
     */
    var id: Long = -1,
    /**
     * 名称
     */
    var name: String = "",
    /**
     * 优先使用本地的包进行传输，而不是让宿主机自己下载
     */
    var useLocalPkg: Boolean = false,
    /**
     * 连接配置
     */
    @OptionTag(converter = HostMachineConnectConfigConverter.Companion.MyConverter::class)
    var connect: HostMachineConnectConfig = EmptyConnectConfig(),
    /**
     * jvm 提供者配置
     */
    @OptionTag(converter = JvmProviderConfigConfigConverter.Companion.MyConverter::class)
    var providers: MutableList<JvmProviderConfig> = mutableListOf(),

    /**
     * 用户自定义搜索组
     */
    var searchGroups: MutableList<JvmSearchGroup> = mutableListOf(),
    /**
     * 数据目录
     */
    var dataDirectory: String = ""
) : DeepCopyable<HostMachineConfig> {

    override fun deepCopy(): HostMachineConfig {
        return HostMachineConfig(
            id,
            name,
            useLocalPkg,
            connect.deepCopy(),
            providers.mapMutable { v -> v.deepCopy() },
            searchGroups.mapMutable { v -> v.deepCopy() },
            dataDirectory
        )
    }

}