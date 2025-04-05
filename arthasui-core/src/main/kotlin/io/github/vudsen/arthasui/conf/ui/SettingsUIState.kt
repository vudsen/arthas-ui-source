package io.github.vudsen.arthasui.conf.ui

import io.github.vudsen.arthasui.conf.HostMachineConfig


data class SettingsUIState (
    var hostMachines: MutableList<HostMachineConfig>
)