package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate

data class JvmContext(
    val template: HostMachineTemplate,
    val providerConfig: JvmProviderConfig,
)