package io.github.vudsen.arthasui.conf

import com.intellij.util.xmlb.annotations.OptionTag
import io.github.vudsen.arthasui.api.DeepCopyable
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.bean.EmptyConnectConfig
import io.github.vudsen.arthasui.common.util.mapMutable
import io.github.vudsen.arthasui.conf.bean.JvmSearchGroup

data class HostMachineConfigV2(
    var name: String = "",
    /**
     * 连接配置
     */
    @OptionTag(converter = HostMachineConnectConfigConverter::class)
    var connect: HostMachineConnectConfig = EmptyConnectConfig(),
    /**
     * jvm 提供者配置
     */
    @OptionTag(converter = JvmProviderConfigConfigConverter::class)
    var providers: MutableList<JvmProviderConfig> = mutableListOf(),

    /**
     * 用户自定义搜索组
     */
    var searchGroups: MutableList<JvmSearchGroup> = mutableListOf()
) : DeepCopyable<HostMachineConfigV2> {

    override fun deepCopy(): HostMachineConfigV2 {
        return HostMachineConfigV2(
            name,
            connect.deepCopy(),
            providers.mapMutable { v -> v.deepCopy() },
            searchGroups.mapMutable { v -> v.deepCopy() }
        )
    }

}