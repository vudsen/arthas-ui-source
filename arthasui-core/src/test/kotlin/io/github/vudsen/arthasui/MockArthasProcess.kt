package io.github.vudsen.arthasui

import io.github.vudsen.arthasui.api.bean.InteractiveShell
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.io.PipedReader
import java.io.PipedWriter
import java.io.Reader
import java.io.Writer

class MockArthasProcess : InteractiveShell {

    companion object {
        const val PS1 = "[arthas@123567]$ "

    }

    private val flow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    inner class PipedOutStreamWrapper(snk: PipedReader) : PipedWriter(snk) {

        override fun flush() {
            super.flush()
            if (!flow.tryEmit(Unit)) {
                println()
            }
        }

    }

    private val serverIn = PipedReader()

    private val serverOut = PipedWriter()

    private val clientOut = PipedOutStreamWrapper(serverIn)

    private val clientIn = PipedReader(serverOut)


    /**
     * 发送准备就绪的消息
     */
    fun sendReadyMessage() {
        serverOut.write("""
            Mock Arthas Process
            $PS1 
        """.trimIndent())
    }

    suspend fun waitClientRequest() {
        val r = flow.first()
        println(r)
    }

    /**
     * 写入响应
     */
    fun writeResponse(content: String) {
        while (serverIn.ready()) {
            val buf = CharArray(32)
            val actualLen = serverIn.read(buf)
            serverOut.write(buf, 0, actualLen)
        }
        serverOut.write(content)
    }

    fun writeResponseEnd() {
        serverOut.write("\n$PS1")
    }

    override fun getReader(): Reader {
        return clientIn
    }

    override fun getWriter(): Writer {
        return clientOut
    }

    override fun isAlive(): Boolean {
        return true
    }

    override fun exitCode(): Int? {
        return 0
    }

    override fun close() {
        clientOut.close()
        clientOut.close()
        serverIn.close()
        serverOut.close()
    }


}