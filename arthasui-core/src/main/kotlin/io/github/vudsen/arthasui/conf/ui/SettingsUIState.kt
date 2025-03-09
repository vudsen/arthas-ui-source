package io.github.vudsen.arthasui.conf.ui

import io.github.vudsen.arthasui.conf.HostMachineConfigV2


data class SettingsUIState (
    var hostMachines: MutableList<HostMachineConfigV2>
)