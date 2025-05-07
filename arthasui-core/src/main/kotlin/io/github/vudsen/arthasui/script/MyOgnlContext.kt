package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.HostMachineConfig

/**
 * Ognl context.
 */
@Suppress("unused")
class MyOgnlContext (
    val template: HostMachine,
    val hostMachineConfig: HostMachineConfig,
) {

    /**
     * Store some helper functions.
     */
    val helpers = LazyLoadHelper(template, hostMachineConfig)

    /**
     * Collect and save result.
     */
    private val resultHolder = ResultHolder()


    /**
     * Add all jvm to [resultHolder].
     */
    fun addAll(jvms: List<JVM>) {
        resultHolder.addAll(jvms)
    }


    /**
     * Generate debug message.
     */
    fun debug(obj: Any) {
        resultHolder.debug(obj)
    }

    /**
     * Get the [ResultHolder].
     */
    fun getResultHolder(): ResultHolder = resultHolder

}