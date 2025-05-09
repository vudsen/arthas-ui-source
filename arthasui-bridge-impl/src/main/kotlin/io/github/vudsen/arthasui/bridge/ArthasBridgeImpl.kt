package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
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
import java.io.Reader
import java.io.Writer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class ArthasBridgeImpl(
    private val arthasProcess: InteractiveShell
) : ArthasBridge {

    companion object {
        private val logger = Logger.getInstance(ArthasBridgeImpl::class.java)

        // ctrl c
        private val EOT = Char(3).toString()
    }

    private val executionLock = ReentrantLock()

    /**
     * 停止运行标志
     */
    @Volatile
    private var stopFlag = false


    private val reader: Reader = arthasProcess.getReader()

    private val writer: Writer = arthasProcess.getWriter()

    /**
     * 解析帧，在写入时应该调用 [onText] 来写入。
     */
    private val outputBuffer = ArthasStreamBuffer()

    private var isAttached = false

    private val readBuffer = CharArray(2048)

    private val listeners = CopyOnWriteArrayList<ArthasBridgeListener>()

    private var lastExecuted: String = ""

    private var exitCode: Int? = null


    private fun ensureAttachStatus() {
        if (isAttached) {
            if (!arthasProcess.isAlive()) {
                stop()
                throw IllegalStateException("Bridge closed.")
            }
            return
        }
        logger.info("Start init the arthas bridge.")
        val result = try {
            parse0(DefaultFrameDecoder())
        } catch (e: Exception) {
            logger.warn("Init bridge failed: ${e.message}")
            stop()
            throw e
        }
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
    private fun writeCommand(command: String, updateLastExecuted: Boolean = true) {
        ensureAttachStatus()
        if (updateLastExecuted) {
            lastExecuted = command
        }
        writer.write(command)
        writer.flush()
    }

    private fun parse0(decoder: ArthasFrameDecoder): ArthasResultItem {
        var data: ArthasResultItem? = null
        val spin = SpinHelper()
        while (true) {
            var len: Int
            while (true) {
                ProgressManager.checkCanceled()
                ensureNotStop()
                if (reader.ready()) {
                    len = reader.read()
                    spin.reportSuccess()
                    break
                } else if (!arthasProcess.isAlive()) {
                    throw CancellationException()
                } else {
                    spin.sleep()
                }
            }
            if (len == -1) {
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
                listener.onFinish(lastExecuted.trim(), result, rawContent)
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

    private fun execute0(command: String): ArthasResultItem {
        val pos = command.indexOf(" ")
        if (pos == -1) {
            writeCommand(command)
            return parse0(DefaultFrameDecoder())
        }

        val item = when (command.substring(0, pos).trim()) {
            "ognl" -> ognl(command)
            "stop" -> {
                stop()
                return StringResult("stop")
            }

            else -> {
                writeCommand(command)
                return parse0(DefaultFrameDecoder())
            }
        }
        return item
    }

    private fun ensureNotStop() {
        if (stopFlag) {
            throw CancellationException()
        }
    }

    override fun execute(command: String): ArthasResultItem {
        val newCommand = if (!command.endsWith('\n')) {
            command + '\n'
        } else {
            command
        }
        logger.debug("Trying to execute command: $command")
        ensureNotStop()
        while (!executionLock.tryLock(1, TimeUnit.SECONDS)) {
            ensureNotStop()
            Thread.sleep(200)
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
    private fun cleanOutput() {
        logger.info("Trying to cancel command: $lastExecuted")
        writeCommand(EOT, false)
        // 读取剩余所有内容
        parse0(DefaultFrameDecoder())
    }

    private fun ognl(command: String): ArthasResultItem {
        writeCommand(command)
        return parse0(OgnlFrameDecoder())
    }


    override fun isClosed(): Boolean {
        if (exitCode != null) {
            return true
        }
        if (arthasProcess.isAlive()) {
            return false
        }
        stop()
        return exitCode != null
    }

    override fun addListener(arthasBridgeListener: ArthasBridgeListener) {
        listeners.add(arthasBridgeListener)
    }

    override fun stop(): Int {
        exitCode?.let { return it }
        logger.info("Stopping arthas...")
        stopFlag = true
        executionLock.lock()
        try {
            exitCode ?.let {
                return it
            }
            if (arthasProcess.isAlive()) {
                try {
                    writer.write("stop\n")
                    writer.flush()
                } catch (_: Exception) { }
            }
            var len: Int
            while (reader.read(readBuffer).also { len = it } != -1) {
                onText(len)
            }
            arthasProcess.close()
            val code = arthasProcess.exitCode() ?: 0
            exitCode = code
            return code
        } finally {
            notifyClosed()
            executionLock.unlock()
        }
    }

    override fun isBusy(): Boolean {
        return executionLock.isLocked() || stopFlag
    }

    private fun notifyClosed() {
        ApplicationManager.getApplication().executeOnPooledThread {
            for (listener in listeners) {
                listener.onClose()
            }
        }
    }


}
