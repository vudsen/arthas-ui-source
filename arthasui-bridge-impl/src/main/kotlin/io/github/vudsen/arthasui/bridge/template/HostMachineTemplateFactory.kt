package io.github.vudsen.arthasui.bridge.template

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig

object HostMachineTemplateFactory {


    fun getHostMachineTemplate(hostMachineConfig: HostMachineConfig, hostMachine: HostMachine): HostMachineTemplate {
        if (hostMachineConfig.connect is LocalConnectConfig) {
            return LocalHostMachineTemplate(hostMachine, hostMachineConfig)
        }
        return LinuxHostMachineTemplate(hostMachine, hostMachineConfig)
    }

}