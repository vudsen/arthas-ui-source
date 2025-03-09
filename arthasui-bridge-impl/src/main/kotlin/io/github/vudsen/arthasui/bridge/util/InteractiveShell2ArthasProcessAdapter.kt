package io.github.vudsen.arthasui.bridge.util

import io.github.vudsen.arthasui.api.ArthasProcess
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import java.io.InputStream
import java.io.OutputStream

class InteractiveShell2ArthasProcessAdapter(private val interactiveShell: InteractiveShell) : ArthasProcess {

    override fun getInputStream(): InputStream {
        return interactiveShell.inputStream
    }

    override fun getOutputStream(): OutputStream {
        return interactiveShell.outputStream
    }

    override fun isAlive(): Boolean {
        return interactiveShell.isAlive()
    }

    override fun stop(): Int {
        return interactiveShell.close()
    }

}