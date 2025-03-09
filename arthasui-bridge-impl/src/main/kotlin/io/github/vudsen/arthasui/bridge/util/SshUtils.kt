package io.github.vudsen.arthasui.bridge.util

import com.intellij.openapi.progress.ProgressManager
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.client.session.ClientSession
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 执行命令，并在执行的时候会调用 [ProgressManager.checkCanceled]
 */
fun ClientSession.executeCancelable(command: String): CommandExecuteResult {
    this.createExecChannel(command).use { exec ->
        val outputStream = ByteArrayOutputStream(256)
        exec.out = outputStream
        exec.err = outputStream
        val future = exec.open()
        while (!future.await(2, TimeUnit.SECONDS)) {
            ProgressManager.checkCanceled()
        }
        while (true) {
            try {
                val events = exec.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), Duration.ofSeconds(1))
                if (!events.contains(ClientChannelEvent.TIMEOUT)) {
                    break
                }
            } catch (e: Exception) {
                ProgressManager.checkCanceled()
            }
        }

        return CommandExecuteResult(outputStream.toString(StandardCharsets.UTF_8), exec.exitStatus)
    }
}

/**
 * 断言执行结果成功
 * @return [CommandExecuteResult.stdout]
 */
fun CommandExecuteResult.ok(): String {
    if (exitCode != 0) {
        throw IllegalStateException("Command execute failed: $exitCode, stdout: $stdout")
    }
    return stdout
}