package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.jetbrains.rd.generator.nova.util.joinToOptString
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.bridge.util.executeCancelable
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import java.util.concurrent.TimeUnit
import kotlin.text.toByteArray

class RemoteSshHostMachineImpl(private val config: SshHostMachineConnectConfig) : CloseableHostMachine {

    companion object {
        val logger = Logger.getInstance(RemoteSshHostMachineImpl::class.java)
    }

    private val session: ClientSession

    init {
        val client = SshClient
            .setUpDefaultClient()

        client.start()
        val session = client.connect(config.ssh.username, config.ssh.host, config.ssh.port)
            .verify(5, TimeUnit.SECONDS)
            .session
        session.addPasswordIdentity(config.ssh.password)
        session.auth().verify(5, TimeUnit.SECONDS)
        logger.info("Successfully connected to ${config.ssh.host}")
        this.session = session
    }


    override fun isClosed(): Boolean {
        val clientSession = session ?: return true
        return clientSession.isClosed
    }

    override fun close() {
        session.close()
        logger.info("Closed connection to ${config.ssh.host}")
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