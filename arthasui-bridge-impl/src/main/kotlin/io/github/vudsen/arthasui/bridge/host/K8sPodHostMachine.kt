package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.kubernetes.client.Copy
import java.io.File
import kotlin.io.path.Path

class K8sPodHostMachine(
    private val jvm: PodJvm,
    private val hostMachine: K8sHostMachine
) : AbstractLinuxShellAvailableHostMachine() {

    override fun execute(vararg command: String): CommandExecuteResult {
        return hostMachine.execute(jvm, *command)
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        return hostMachine.createInteractiveShell(jvm, *command)
    }

    override fun transferFile(
        src: String,
        dest: String,
        indicator: ProgressIndicator?
    ) {
        val file = File(src)
        if (file.length() == 0L) {
            return
        }
        indicator?.let {
            it.pushState()
            it.text = "Uploading ${file.name} to $dest"
        }
        try {
            // TODO 使用 websocket 手动上传，支持进度条
            Copy(hostMachine.apiClient).copyFileToPod(jvm.namespace, jvm.id, jvm.containerName, Path(src), Path(dest))
        } finally {
            indicator?.popState()
        }

    }

    override fun getOS(): OS {
        return OS.LINUX
    }

    override fun getConfiguration(): HostMachineConnectConfig {
        return hostMachine.getConfiguration()
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return hostMachine.getHostMachineConfig()
    }

}