package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.conf.HostMachineConfigV2

/**
 * 提供所有可执行文件的路径
 */
class LocalJvmSearchHelper(hostMachineConfig: HostMachineConfigV2) {

    private var provider: LocalJvmProviderConfig? = null

    init {
        for (entity in hostMachineConfig.providers) {
            if (entity is LocalJvmProviderConfig) {
                provider = entity
                break
            }
        }
    }


    fun jps(): String {
        provider ?.let {
            return "${it.jdkHome}/bin/jps"
        } ?: throw IllegalStateException("LocalJvmProvider is configured.")
    }


}