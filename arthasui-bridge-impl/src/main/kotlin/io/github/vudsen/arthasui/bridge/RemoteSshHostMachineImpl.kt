package io.github.vudsen.arthasui.bridge

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

    val session: ClientSession by lazy {
        val client = SshClient
            .setUpDefaultClient()

        client.start()
        val session = client.connect(config.ssh.username, config.ssh.host, config.ssh.port)
            .verify(5, TimeUnit.SECONDS)
            .session
        session.addPasswordIdentity(config.ssh.password)
        session.auth().verify(5, TimeUnit.SECONDS)
        session
    }

    private var isClosed = false

    override fun isClosed(): Boolean {
        return isClosed
    }

    override fun close() {
        isClosed = true
        val lazy = this::session.getDelegate() as Lazy<*>
        if (lazy.isInitialized()) {
            session.close()
        }
    }

    override fun execute(vararg command: String): CommandExecuteResult {
        return session.executeCancelable(command.joinToOptString(" "))
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        val channel = session.createShellChannel()
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
                return@InteractiveShell channel.exitStatus
            }
            try {
                channel.close()
            } catch (_: Exception) { }
            return@InteractiveShell channel.exitStatus
        }
    }

    override fun getOS(): OS {
        return config.os
    }

}