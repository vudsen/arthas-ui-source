package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.currentOS
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit


class LocalHostMachineImpl(private val connectConfig: LocalConnectConfig) : HostMachine {

    private val os = currentOS()

    companion object {
        private val logger = Logger.getInstance(LocalHostMachineImpl::class.java.name)
    }

    override fun execute(vararg command: String): CommandExecuteResult {
        val process = try {
            ProcessBuilder(*command).redirectErrorStream(true).start()
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                logger.error("Failed to execute command: $command", e)
            }
            return CommandExecuteResult(e.message ?: "<Unknown>", 1)
        }
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

    override fun transferFile(src: String, dest: String) {
        val actualDest = if (File(dest).isDirectory) {
            val name = File(src).name
            "$dest/$name"
        } else {
            dest
        }
        FileInputStream(src).channel.use { ins ->
            FileOutputStream(actualDest).channel.use { out ->
                out.transferFrom(ins, 0, ins.size())
            }
        }
    }



    override fun getConfiguration(): HostMachineConnectConfig {
        return connectConfig
    }


}