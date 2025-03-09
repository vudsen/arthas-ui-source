package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.progress.ProgressManager
import com.jetbrains.rd.generator.nova.util.joinToOptString
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.bridge.util.executeCancelable
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import java.util.concurrent.TimeUnit
import kotlin.text.toByteArray

class RemoteSshHostMachineImpl(private val config: SshHostMachineConnectConfig) : HostMachine, CloseableHostMachine {

    private var _session: ClientSession? = null

    private fun getSession(): ClientSession {
        _session ?.let { return it }
        synchronized(this) {
            _session ?.let { return it }
            val client = SshClient
                .setUpDefaultClient()

            client.start()
            val session = client.connect(config.ssh.username, config.ssh.host, config.ssh.port)
                .verify(5, TimeUnit.SECONDS)
                .session
            session.addPasswordIdentity(config.ssh.password)
            session.auth().verify(5, TimeUnit.SECONDS)
            this._session = session
            return session
        }
    }

    override fun isClosed(): Boolean {
        val clientSession = _session ?: return true
        return clientSession.isClosed
    }

    override fun close() {
        _session ?: return
        synchronized(this) {
            val clientSession = _session ?: return
            clientSession.close()
            _session = null
        }
    }

    override fun execute(vararg command: String): CommandExecuteResult {
        return getSession().executeCancelable(command.joinToOptString(" "))
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        val channel = getSession().createShellChannel()
        channel.isRedirectErrorStream = true
        val future = channel.open()
        for (spin in 0 until 20) {
            if (future.await(1, TimeUnit.SECONDS)) {
                break
            }
        }
        if (!future.isDone) {
            TODO("Tip user connect failed.")
        }
        channel.invertedIn.write((command.joinToOptString(" ") + '\n').toByteArray())
        channel.invertedIn.flush()
        return InteractiveShell(channel.invertedOut, channel.invertedIn, { !channel.isClosed && !channel.isClosing }) {
            if (channel.isClosed) {
                return@InteractiveShell 0
            }
            val closeFuture = channel.close(true)
            while (!closeFuture.await(1, TimeUnit.SECONDS)) {
                ProgressManager.checkCanceled()
            }
            return@InteractiveShell 0
        }
    }

    override fun getOS(): OS {
        return config.os
    }

}