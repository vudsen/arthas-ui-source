package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig

data class VirtualFileAttributes(
    val jvm: JVM,
    val connectConfig: HostMachineConnectConfig,
    val providerConfig: JvmProviderConfig
)

