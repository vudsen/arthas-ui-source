package io.github.vudsen.arthasui.conf

import com.intellij.util.xmlb.annotations.OptionTag
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.bean.EmptyConnectConfig

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
    var providers: MutableList<JvmProviderConfig> = mutableListOf()
)