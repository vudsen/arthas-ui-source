package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressManager
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.currentOS
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit


class LocalHostMachineImpl : HostMachine {

    private val os = currentOS()

    override fun execute(vararg command: String): CommandExecuteResult {
        val process = ProcessBuilder(*command).redirectErrorStream(true).start()
        val baos = ByteArrayOutputStream(512)
        val buf = ByteArray(512)
        while (true) {
            ProgressManager.checkCanceled()
            if (process.inputStream.read(buf).also { readBytes ->
                    if (readBytes == -1) {
                        return@also
                    }
                    baos.write(buf, 0, readBytes)
                } == -1 && process.waitFor(1, TimeUnit.SECONDS)) {
                break
            }
        }
        // Ensure all remaining data is read
        while (process.inputStream.read(buf).also { readBytes ->
                if (readBytes == -1) return@also
                baos.write(buf, 0, readBytes)
            } != -1) {
            ProgressManager.checkCanceled()
        }
        return CommandExecuteResult(baos.toString(), process.exitValue())
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        val process = ProcessBuilder(*command).redirectErrorStream(true).start()
        return InteractiveShell(process.inputStream, process.outputStream, { process.isAlive }) {
            if (process.isAlive) {
                return@InteractiveShell process.exitValue()
            }
            try {
                process.destroy()
            } catch (_: Exception) { }
            return@InteractiveShell process.exitValue()
        }
    }

    override fun getOS(): OS {
        return os
    }


}