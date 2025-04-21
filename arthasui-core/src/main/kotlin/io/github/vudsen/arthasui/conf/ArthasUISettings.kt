package io.github.vudsen.arthasui.conf

import io.github.vudsen.arthasui.api.DeepCopyable
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.util.deepCopy


class ArthasUISettings(
    /**
     * 宿主机
     */
    var hostMachines: MutableList<HostMachineConfig> = mutableListOf()
) : DeepCopyable<ArthasUISettings> {


    override fun deepCopy(): ArthasUISettings {
        return ArthasUISettings(hostMachines.deepCopy())
    }

}