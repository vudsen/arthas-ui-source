package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.bridge.bean.LocalJVM
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import io.github.vudsen.arthasui.script.helper.LocalJvmSearchHelper

@Suppress("unused")
class MyOgnlContext (
    val hostMachine: HostMachine,
    val hostMachineConfig: HostMachineConfigV2,
) {

    val helpers = LazyLoadHelper(hostMachine, hostMachineConfig)

    /**
     * 保存执行结果
     */
    private val resultHolder = ResultHolder()


    fun addAll(jvms: List<JVM>) {
        resultHolder.addAll(jvms)
    }


    /**
     * debug
     */
    fun debug(obj: Any) {
        resultHolder.debug(obj)
    }

    /**
     * 获取执行结果
     */
    fun getResultHolder(): ResultHolder = resultHolder

}