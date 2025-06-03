package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.bridge.util.KubectlClient

class K8sPodHostMachine(
    private val jvm: PodJvm,
    private val providerConfig: K8sJvmProviderConfig,
    private val hostMachine: ShellAvailableHostMachine
) : AbstractLinuxShellAvailableHostMachine() {

    private val client = KubectlClient(hostMachine, providerConfig, jvm.containerName)

    override fun execute(vararg command: String): CommandExecuteResult {
        return client.execute("-n", jvm.namespace, "pod", jvm.id, "--", *command)
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        return client.createInteractiveShell("-n", jvm.namespace, "pod", jvm.id, "--", *command)
    }

    override fun isArm(): Boolean {
        return providerConfig.isArm
    }

    /**
     * 支持直接传输文件夹
     */
    override fun transferFile(
        src: String,
        dest: String,
        indicator: ProgressIndicator?
    ) {
        // https://github.com/kubernetes/kubernetes/issues/77310
        // The src can not contain colon, and it must locate in `c:/`
        val actualSrc: String = if (src.startsWith("C:\\") || src.startsWith("c:\\")) {
            src.substring(3)
        } else {
            src
        }
        indicator?.let {
            it.pushState()
            it.text = "Copying ${src} to ${dest}..."
        }
        try {
            client.execute("cp", actualSrc, "${jvm.id}:${dest}", "-n", jvm.namespace).ok()
        } finally {
            indicator?.popState()
        }
    }


    override fun getConfiguration(): HostMachineConnectConfig {
        return hostMachine.getConfiguration()
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return hostMachine.getHostMachineConfig()
    }


}