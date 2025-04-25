package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.jetbrains.rd.generator.nova.util.joinToOptString
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.util.executeCancelable
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.Factory
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory
import org.apache.sshd.common.util.threads.CloseableExecutorService
import org.apache.sshd.sftp.client.SftpClient
import org.apache.sshd.sftp.client.SftpClientFactory
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path


class RemoteSshHostMachineImpl(private val config: SshHostMachineConnectConfig, executorServiceFactory: Factory<CloseableExecutorService>) : CloseableHostMachine {

    companion object {
        val logger = Logger.getInstance(RemoteSshHostMachineImpl::class.java)
    }

    private val session: ClientSession

    init {
        val client = SshClient
            .setUpDefaultClient()

        client.ioServiceFactoryFactory = Nio2ServiceFactoryFactory(executorServiceFactory)
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
        val clientSession = session
        return clientSession.isClosed
    }

    override fun close() {
        session.close()
        logger.info("Connection closed: ${config.ssh.host}")
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

    override fun transferFile(src: String, dest: String, indicator: ProgressIndicator?) {
        val file = File(src)
        if (file.length() == 0L) {
            return
        }
        indicator?.text = "Uploading ${file.name} to $dest"
        val total = file.length().toDouble()
        val totalMb = String.format("%.2f", total / 1024 / 1024)
        var written = 0L
        SftpClientFactory.instance().createSftpClient(session).use { client ->
            client.open(dest, listOf(SftpClient.OpenMode.Write, SftpClient.OpenMode.Create)).use { handle ->
                file.inputStream().use { input ->
                    val buf = ByteArray((1024 * 1024L).coerceAtMost(file.length()).toInt())
                    var len: Int
                    while (input.read(buf).also { len = it } != -1) {
                        ProgressManager.checkCanceled()
                        client.write(handle, written, buf, 0, len)
                        indicator?.fraction = written / total
                        indicator?.text = "Uploading ${file.name} to $dest (${String.format("%.2f", written.toDouble() / 1024 / 1024)}MB / ${totalMb}MB)"
                        written += len
                    }
                }
            }
        }
    }


    override fun getConfiguration(): HostMachineConnectConfig {
        return config
    }


    override fun toString(): String {
        return "RemoteSshHostMachineImpl(name = ${config.name}, host=${config.ssh.host}, port=${config.ssh.port})"
    }


}