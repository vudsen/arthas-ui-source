package io.github.vudsen.arthasui

import io.github.vudsen.arthasui.api.bean.InteractiveShell
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.charset.StandardCharsets

class MockArthasProcess : InteractiveShell {

    companion object {
        const val PS1 = "[arthas@123567]$ "

    }

    private val flow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    inner class PipedOutStreamWrapper(snk: PipedInputStream) : PipedOutputStream(snk) {

        override fun flush() {
            super.flush()
            if (!flow.tryEmit(Unit)) {
                println()
            }
        }

    }

    private val serverIn = PipedInputStream()

    private val serverOut = PipedOutputStream()

    private val clientOut = PipedOutStreamWrapper(serverIn)

    private val clientIn = PipedInputStream(serverOut)


    /**
     * 发送准备就绪的消息
     */
    fun sendReadyMessage() {
        serverOut.write("""
            Mock Arthas Process
            $PS1 
        """.trimIndent().toByteArray(StandardCharsets.UTF_8))
    }

    suspend fun waitClientRequest() {
        val r = flow.first()
        println(r)
    }

    /**
     * 写入响应
     */
    fun writeResponse(content: String) {
        var len = serverIn.available()
        if (len > 0) {
            val buf = ByteArray(32)
            while (len > 0) {
                val actualLen = serverIn.read(buf)
                serverOut.write(buf, 0, actualLen)
                len = serverIn.available()
            }
        }
        serverOut.write(content.toByteArray(StandardCharsets.UTF_8))
    }

    fun writeResponseEnd() {
        serverOut.write("\n$PS1".toByteArray(StandardCharsets.UTF_8))
    }

    override fun getInputStream(): InputStream {
        return clientIn
    }

    override fun getOutputStream(): OutputStream {
        return clientOut
    }

    override fun isAlive(): Boolean {
        return true
    }

    override fun exitCode(): Int? {
        return 0
    }

    override fun close() {}


}