package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import io.github.vudsen.arthasui.script.helper.DockerSearchHelper
import io.github.vudsen.arthasui.script.helper.LocalJvmSearchHelper

class LazyLoadHelper(private val hostMachine: HostMachine, private val hostMachineConfig: HostMachineConfigV2) {

    private var _local: LocalJvmSearchHelper? = null

    private var _docker: DockerSearchHelper? = null

    @Suppress("unused")
    fun local(): LocalJvmSearchHelper {
        _local ?.let { return it }
        val instance = LocalJvmSearchHelper(hostMachine, hostMachineConfig)
        _local = instance
        return instance
    }

    @Suppress("unused")
    fun docker(): DockerSearchHelper {
        _docker ?.let { return it }
        val instance = DockerSearchHelper(hostMachine)
        _docker = instance
        return instance
    }
}