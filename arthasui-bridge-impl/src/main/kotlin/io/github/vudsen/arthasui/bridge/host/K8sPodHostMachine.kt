package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream

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
        var process: Process? = null
        try {
            process =
                hostMachine.createOriginalInteractiveShell(jvm, "sh", "-c", "cat > ${dest}")
            var written = 0L
            val total = file.length().toDouble()
            val totalMb = String.format("%.2f", total / 1024 / 1024)
            FileInputStream(src).use { input ->
                val bos = process.outputStream
                val buf = ByteArray((file.length() / 2).coerceAtMost(1 * 1024 * 1024).toInt())
                var len: Int
                while (input.read(buf).also { len = it } != -1) {
                    ProgressManager.checkCanceled()
                    bos.write(buf, 0, len)
                    indicator?.let {
                        it.fraction = written / total
                        it.text = "Uploading ${file.name} to $dest (${
                            String.format(
                                "%.2f",
                                written.toDouble() / 1024 / 1024
                            )
                        }MB / ${totalMb}MB)"
                    }
                    written += len
                }
            }
        } finally {
            process?.destroy()
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