package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig

data class VirtualFileAttributes(
    val jvm: JVM,
    val hostMachineConfig: HostMachineConfig,
    val providerConfig: JvmProviderConfig
)

