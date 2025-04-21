package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate

@Suppress("unused")
class MyOgnlContext (
    val template: HostMachineTemplate,
    val hostMachineConfig: HostMachineConfig,
) {

    val helpers = LazyLoadHelper(template, hostMachineConfig)

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