package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.bean.InteractiveShell
import java.io.Reader
import java.io.Writer

class StandardInteractiveShell(private val pro: Process) : InteractiveShell {

    override fun getReader(): Reader {
        return pro.inputReader()
    }

    override fun getWriter(): Writer {
        return pro.outputWriter()
    }

    override fun isAlive(): Boolean {
        return pro.isAlive
    }

    override fun exitCode(): Int? {
        try {
            return pro.exitValue()
        } catch (e: IllegalThreadStateException) {
            return null
        }
    }


    override fun close() {
        if (!pro.isAlive) {
            return
        }
        try {
            pro.destroy()
            pro.outputStream.close()
            pro.inputStream.close()
        } catch (_: Exception) { }
    }
}