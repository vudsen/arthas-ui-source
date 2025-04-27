package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.common.parser.ArthasFrameDecoder
import io.github.vudsen.arthasui.common.parser.DefaultFrameDecoder
import io.github.vudsen.arthasui.common.parser.OgnlFrameDecoder
import io.github.vudsen.arthasui.common.bean.StringResult
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.common.lang.ArthasStreamBuffer
import io.github.vudsen.arthasui.common.util.SpinHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.text.toByteArray

class ArthasBridgeImpl(
    private val arthasProcess: InteractiveShell
) : ArthasBridge {

    private val executionLock = Mutex()


    /**
     * 停止运行标志
     */
    @Volatile
    private var stopFlag = false

    companion object {
        private val logger = Logger.getInstance(ArthasBridgeImpl::class.java)
        // ctrl c
        private val EOT = Char(3).toString()
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
                val readCharSequence = reader.readText()
                throw IllegalStateException(readCharSequence)
            }
            return
        }
        logger.info("Start init the arthas bridge.")
        val result = parse0(DefaultFrameDecoder(), true)
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
     * 写入命令.
     * @param command 命令，写入前需要按需对命令追加 \n
     * @param updateLastExecuted 是否记录命令的变更
     */
    private suspend fun writeCommand(command: String, updateLastExecuted: Boolean = true) {
        ensureAttachStatus()
        if (updateLastExecuted) {
            lastExecuted = command
        }
        withContext(Dispatchers.IO) {
            outputStream.write(command.toByteArray())
            outputStream.flush()
        }
    }

    private suspend fun parse0(decoder: ArthasFrameDecoder, handleStop: Boolean): ArthasResultItem {
        var data: ArthasResultItem? = null
        val spinHelper = SpinHelper()
        while (true) {
            val len: Int = withContext(Dispatchers.IO) {
                val len: Int
                while (true) {
                    ensureActive()
                    if (handleStop) {
                        ensureNotStop()
                    }
                    if (reader.ready()) {
                        spinHelper.reportSuccess()
                        len = reader.read(readBuffer)
                        break
                    } else {
                        spinHelper.sleepSuspend()
                    }
                }
                return@withContext len
            }
            if (len == -1) {
                // canceled.
                throw CancellationException()
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

    private suspend fun execute0(command: String): ArthasResultItem {
        val pos = command.indexOf(" ")
        if (pos == -1) {
            writeCommand(command)
            return parse0(DefaultFrameDecoder(), true)
        }

        val item = when (command.substring(0, pos).trim()) {
            "ognl" -> ognl(command)
            else -> {
                writeCommand(command)
                return parse0(DefaultFrameDecoder(), true)
            }
        }
        return item
    }

    private fun ensureNotStop() {
        if (stopFlag) {
            throw CancellationException()
        }
    }

    override suspend fun execute(command: String): ArthasResultItem {
        val newCommand = if (!command.endsWith('\n')) {
            command + '\n'
        } else {
            command
        }
        logger.debug("Trying to execute command: $command")
        ensureNotStop()
        val spinHelper = SpinHelper()
        while (!executionLock.tryLock()) {
            ensureNotStop()
            spinHelper.sleepSuspend()
            logger.debug("Failed to acquire lock for command: $command")
        }
        if (stopFlag) {
            executionLock.unlock()
            throw CancellationException()
        }
        try {
            return execute0(newCommand)
        } catch (e: CancellationException) {
            cleanOutput()
            throw e
        } finally {
            executionLock.unlock()
        }
    }

    /**
     * 当命令被取消后，做收尾操作.
     */
    private suspend fun cleanOutput() {
        withContext(NonCancellable) {
            logger.info("Trying to cancel command: $lastExecuted")
            writeCommand(EOT, false)
            // 读取剩余所有内容
            parse0(DefaultFrameDecoder(), true)
        }
    }

    private suspend fun ognl(command: String): ArthasResultItem {
        writeCommand(command)
        return parse0(OgnlFrameDecoder(), true)
    }

    override fun isAlive(): Boolean {
        return arthasProcess.isAlive()
    }

    override fun addListener(arthasBridgeListener: ArthasBridgeListener) {
        listeners.add(arthasBridgeListener)
    }

    override fun stop(): Int {
        logger.info("Stopping arthas...")
        stopFlag = true
        runBlocking {
            val spinHelper = SpinHelper()
            if (!executionLock.tryLock()) {
                spinHelper.sleepSuspend()
            }
            withContext(NonCancellable) {
                writeCommand("stop\n", true)
                // 读取剩余所有内容
                parse0(DefaultFrameDecoder(), false)
            }
        }
        try {
            arthasProcess.close()
            return arthasProcess.exitCode()!!
        } finally {
            ApplicationManager.getApplication().executeOnPooledThread {
                for (listener in listeners) {
                    listener.onClose()
                }
            }
        }
    }



}
