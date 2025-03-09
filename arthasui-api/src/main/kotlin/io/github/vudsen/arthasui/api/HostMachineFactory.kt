package io.github.vudsen.arthasui.api

import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig

interface HostMachineFactory {

    fun getHostMachine(connectConfig: HostMachineConnectConfig): HostMachine

}