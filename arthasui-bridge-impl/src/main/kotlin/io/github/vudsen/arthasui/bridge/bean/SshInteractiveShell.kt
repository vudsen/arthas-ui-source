package io.github.vudsen.arthasui.bridge.bean

import com.intellij.openapi.progress.ProgressManager
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import org.apache.sshd.client.channel.ChannelShell
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class SshInteractiveShell(private val channel: ChannelShell) : InteractiveShell {

    override fun getInputStream(): InputStream {
        return channel.invertedOut
    }

    override fun getOutputStream(): OutputStream {
        return channel.invertedIn
    }

    override fun isAlive(): Boolean {
        return !channel.isClosed
    }

    override fun exitCode(): Int? {
        return channel.exitStatus
    }

    override fun close() {
        if (channel.isClosed) {
            return
        }
        val closeFuture = channel.close(true)
        while (!closeFuture.await(1, TimeUnit.SECONDS)) {
            ProgressManager.checkCanceled()
        }
    }

}