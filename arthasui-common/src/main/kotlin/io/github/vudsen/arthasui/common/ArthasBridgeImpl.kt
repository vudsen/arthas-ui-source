package io.github.vudsen.arthasui.common

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.io.readCharSequence
import com.intellij.openapi.diagnostic.Logger;
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasProcess
import io.github.vudsen.arthasui.common.parser.ArthasFrameDecoder
import io.github.vudsen.arthasui.common.parser.DefaultFrameDecoder
import io.github.vudsen.arthasui.common.parser.OgnlFrameDecoder
import io.github.vudsen.arthasui.common.bean.StringResult
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.common.lang.ArthasStreamBuffer
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.text.toByteArray

class ArthasBridgeImpl(
    private val arthasProcess: ArthasProcess
) : ArthasBridge {


    companion object {
        private val logger = Logger.getInstance(ArthasBridgeImpl::class.java)
        val EOT = Char(3).toString()
    }

    private val reader: Reader = InputStreamReader(arthasProcess.getInputStream())

    private val outputStream: OutputStream = arthasProcess.getOutputStream()

    /**
     * 解析帧，在写入时应该调用 [onText] 来写入。
     */
    private val outputBuffer = ArthasStreamBuffer()

    private var isAttached = false

    private val readBuffer = CharArray(512)

    private val listeners = CopyOnWriteArrayList<ArthasBridgeListener>()

    private var lastExecuted: String = ""

    private suspend fun ensureAttachStatus() {
        if (isAttached) {
            if (!arthasProcess.isAlive()) {
                val readCharSequence = reader.readCharSequence()
                throw IllegalStateException(readCharSequence.toString())
            }
            return
        }
        val result = parse0(DefaultFrameDecoder())
        if (logger.isDebugEnabled) {
            logger.debug(result.toString())
        }
        isAttached = true
    }

    private fun debugDisplay(len: Int, pos: String) {
        logger.debug("===============================(len = $len, pos = $pos)")
        val result = StringBuilder(len)
        for (i in 0 until len) {
            val c = readBuffer[i]
            if (c == '\n') {
                result.append("\\n")
            } else if (c == '\r') {
                result.append("\\r")
            } else {
                result.append(c)
            }
        }
        logger.debug(result.toString())
    }

    /**
     * 执行命令
     * @param command 命令
     * @param cb 回调函数，当读取到新的内容时会调用，此时需要在回调中尝试解析出一帧
     */
    private suspend fun execute0(command: String) {
        ensureAttachStatus()
        lastExecuted = command
        withContext(Dispatchers.IO) {
            outputStream.write(command.toByteArray())
            outputStream.write("\r\n".toByteArray())
            outputStream.flush()
        }
    }

    private suspend fun parse0(decoder: ArthasFrameDecoder): ArthasResultItem {
        var len: Int
        var data: ArthasResultItem? = null
        while (true) {
            withContext(Dispatchers.IO) {
                len = reader.read(readBuffer)
            }
            onText(len)
            if (len == 0) {
                break
            }
            val frame = outputBuffer.readNextFrame()
            if (frame != null) {
                try {
                    data = decoder.parse(frame)
                    notifyFinish(data, frame)
                } catch (e: Exception) {
                    notifyError(frame, e)
                    return StringResult(frame)
                }
                break
            }
        }
        if (data == null) {
            TODO("Reset frame and tip user")
        }
        return data
    }

    private fun notifyError(frame: String, exception: Exception) {
        if (lastExecuted == "") {
            return
        }
        ApplicationManager.getApplication().executeOnPooledThread {
            for (listener in listeners) {
                listener.onError(lastExecuted, frame, exception)
            }
        }
    }

    private fun notifyFinish(result: ArthasResultItem, rawContent: String) {
        if (lastExecuted == "") {
            return
        }
        ApplicationManager.getApplication().executeOnPooledThread {
            for (listener in listeners) {
                listener.onFinish(lastExecuted, result, rawContent)
            }
        }
    }

    private fun onText(len: Int) {
        if (len <= 0) {
            return
        }
        // 当指令过长时，会莫名其妙出现一个 " \r"，除了 macOS，都需要将其替换为换行
        if (logger.isDebugEnabled) {
            debugDisplay(len, "before")
        }
        val actualLen = ensureContentSafe(len)
        if (logger.isDebugEnabled) {
            debugDisplay(actualLen, "after")
        }
        outputBuffer.write(readBuffer, actualLen)
        val s = String(readBuffer, 0, actualLen)

        for (listener in listeners) {
            listener.onContent(s)
        }
    }

    /**
     * 移除所有 `\r`, 换行符全部替换为 \n
     */
    private fun ensureContentSafe(len: Int): Int {
        var actualLen = len
        // TODO check performance on MacOS
        var notUpdate = true
        for (i in 0 until len) {
            if (readBuffer[i] != '\r') {
                continue
            }
            readBuffer[i] = 0.toChar()
            notUpdate = false
        }
        if (notUpdate) {
            return len
        }

        // compact
        var search = 0
        for (i in 0 until len) {
            if (readBuffer[i] == 0.toChar()) {
                search = Math.max(search, i + 1)
                var notFound = true
                while (search < len) {
                    if (readBuffer[search] != 0.toChar()) {
                        readBuffer[i] = readBuffer[search]
                        readBuffer[search] = 0.toChar()
                        search++
                        notFound = false
                        break
                    }
                    search++
                }
                if (notFound) {
                    break
                }
            }
            if (readBuffer[i] != 0.toChar()) {
                actualLen = i
            }
        }
        return actualLen + 1
    }


    override suspend fun execute(command: String): ArthasResultItem {
        val pos = command.indexOf(" ")
        if (pos == -1) {
            execute0(command)
            return parse0(DefaultFrameDecoder())
        }

        val item = when (command.substring(0, pos).trim()) {
            "ognl" -> ognl(command)
            else -> {
                execute0(command)
                return parse0(DefaultFrameDecoder())
            }
        }
        return item

    }

    private suspend fun ognl(command: String): ArthasResultItem {
        execute0(command)
        return parse0(OgnlFrameDecoder())
    }

    override fun isAlive(): Boolean {
        return arthasProcess.isAlive()
    }

    override fun addListener(arthasBridgeListener: ArthasBridgeListener) {
        listeners.add(arthasBridgeListener)
    }

    override fun stop(): Int {
        logger.info("Stopping arthas...")
        runBlocking {
            execute("stop\n")
        }
        try {
            return arthasProcess.stop()
        } finally {
            ApplicationManager.getApplication().executeOnPooledThread {
                for (listener in listeners) {
                    listener.onClose()
                }
            }
        }
    }

    override suspend fun cancel() {
        execute0(EOT)
        var len: Int;
        while (true) {
            withContext(Dispatchers.IO) {
                len = if (reader.ready()) {
                    reader.read(readBuffer)
                } else {
                    -1;
                }
            }
            if (len == -1) {
                break
            }
            onText(len)
        }
        outputBuffer.clear()
    }




}