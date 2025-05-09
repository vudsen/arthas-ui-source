package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.bean.InteractiveShell
import java.io.InputStream
import java.io.OutputStream

class LocalInteractiveShell(private val process: Process) : InteractiveShell {

    override fun getInputStream(): InputStream {
        return process.inputStream
    }

    override fun getOutputStream(): OutputStream {
        return process.outputStream
    }

    override fun isAlive(): Boolean {
        return process.isAlive
    }

    override fun exitCode(): Int? {
        try {
            return process.exitValue()
        } catch (e: IllegalThreadStateException) {
            return null
        }
    }


    override fun close() {
        if (!process.isAlive) {
            return
        }
        try {
            process.destroy()
            process.outputStream.close()
            process.inputStream.close()
        } catch (_: Exception) { }
    }
}