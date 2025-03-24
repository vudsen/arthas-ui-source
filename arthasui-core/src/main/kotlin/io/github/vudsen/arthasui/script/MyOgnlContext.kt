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

    /**
     * 用于帮助搜素本地 JVM
     */
    val localHelper: LocalJvmSearchHelper = LocalJvmSearchHelper(hostMachine, hostMachineConfig)

    val helpers = LazyLoadHelper(hostMachine, hostMachineConfig)

    /**
     * 保存执行结果
     */
    private val resultHolder = ResultHolder()

    /**
     * 添加一个本地 JVM.
     * @param pidNameArray 一个二维数组，内部数组的长度应该为 2，第一个参数为 jvm 的pid，第二个为名称
     */
    fun addLocal(pidNameArray: List<Array<String>>) {
        for (arr in pidNameArray) {
            if (arr.size < 2) {
                continue
            }
            resultHolder.add(LocalJVM(arr[0], arr[1]))
        }
    }

    fun addAll(jvms: List<JVM>) {
        resultHolder.addAll(jvms)
    }

    /**
     * 添加一个 docker 中的 jvm
     */
    fun addDocker(pid: String, name: String?) {
        resultHolder.add(DockerJvm(pid, name ?: "<null>"))
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