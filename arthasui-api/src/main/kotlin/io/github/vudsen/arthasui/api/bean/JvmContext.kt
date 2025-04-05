package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig

data class JvmContext(
    val hostMachine: HostMachine,
    val providerConfig: JvmProviderConfig,
)