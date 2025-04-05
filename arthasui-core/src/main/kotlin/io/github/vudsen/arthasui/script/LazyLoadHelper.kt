package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.conf.HostMachineConfig
import io.github.vudsen.arthasui.script.helper.DockerSearchHelper
import io.github.vudsen.arthasui.script.helper.LocalJvmSearchHelper

class LazyLoadHelper(private val hostMachine: HostMachine, private val hostMachineConfig: HostMachineConfig) {

    private var _local: LocalJvmSearchHelper? = null

    private var _docker: DockerSearchHelper? = null

    @Suppress("unused")
    fun local(): LocalJvmSearchHelper {
        _local ?.let { return it }
        val providerConfig = hostMachineConfig.providers.find { v -> v::class.java == LocalJvmProviderConfig::class.java }
        if (providerConfig == null) {
            throw IllegalStateException("No Docker provider found for this host machine")
        }
        val instance = LocalJvmSearchHelper(hostMachine, providerConfig as LocalJvmProviderConfig)
        _local = instance
        return instance
    }

    @Suppress("unused")
    fun docker(): DockerSearchHelper {
        _docker ?.let { return it }
        val providerConfig = hostMachineConfig.providers.find { v -> v::class.java == JvmInDockerProviderConfig::class.java }
        if (providerConfig == null) {
            throw IllegalStateException("No Docker provider found for this host machine")
        }
        val instance = DockerSearchHelper(hostMachine, providerConfig as JvmInDockerProviderConfig)
        _docker = instance
        return instance
    }
}