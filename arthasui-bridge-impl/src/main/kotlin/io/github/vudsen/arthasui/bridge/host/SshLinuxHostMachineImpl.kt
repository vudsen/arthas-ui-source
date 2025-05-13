package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.jetbrains.rd.generator.nova.util.joinToOptString
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.util.RefreshState
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.Factory
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory
import org.apache.sshd.common.util.threads.CloseableExecutorService
import org.apache.sshd.sftp.client.SftpClient
import org.apache.sshd.sftp.client.SftpClientFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.Reader
import java.io.Writer
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.EnumSet
import java.util.concurrent.TimeUnit

/**
 * 使用 SSH 连接 Linux 机器
 */
class SshLinuxHostMachineImpl(
    private val config: HostMachineConfig,
    executorServiceFactory: Factory<CloseableExecutorService>
) : CloseableHostMachine, AbstractLinuxShellAvailableHostMachine() {

    private val connectConfig = config.connect as SshHostMachineConnectConfig

    companion object {
        val logger = Logger.getInstance(SshLinuxHostMachineImpl::class.java)

        private class SshInteractiveShell(
            private val channel: ChannelExec,
            actualIn: InputStream,
            actualOut: OutputStream
        ) : InteractiveShell {

            private val reader = InputStreamReader(actualIn)

            private val writer = OutputStreamWriter(actualOut)

            override fun getReader(): Reader {
                return reader
            }

            override fun getWriter(): Writer {
                return writer
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
                channel.close(true).await()
                reader.close()
                // stream is already closed
                // writer.close()
            }

        }
    }

    private val session: ClientSession

    init {
        val client = SshClient
            .setUpDefaultClient()

        client.ioServiceFactoryFactory = Nio2ServiceFactoryFactory(executorServiceFactory)
        client.start()
        val session = client.connect(connectConfig.ssh.username, connectConfig.ssh.host, connectConfig.ssh.port)
            .verify(5, TimeUnit.SECONDS)
            .session
        session.addPasswordIdentity(connectConfig.ssh.password)
        session.auth().verify(5, TimeUnit.SECONDS)
        logger.info("Successfully connected to ${connectConfig.ssh.host}")
        this.session = session
    }


    override fun isClosed(): Boolean {
        val clientSession = session
        return clientSession.isClosed
    }

    override fun close() {
        session.close()
        logger.info("Connection closed: ${connectConfig.ssh.host}")
    }

    @RefreshState
    override fun execute(vararg command: String): CommandExecuteResult {
        session.createExecChannel(command.joinToOptString(" ")).use { exec ->
            val outputStream = ByteArrayOutputStream(1024)
            exec.isRedirectErrorStream = true
            exec.out = outputStream
            val future = exec.open()
            while (!future.await(2, TimeUnit.SECONDS)) {
                ProgressManager.checkCanceled()
            }
            while (true) {
                val events = exec.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), Duration.ofSeconds(1))
                if (events.contains(ClientChannelEvent.CLOSED)) {
                    break
                }
                ProgressManager.checkCanceled()
            }
            return CommandExecuteResult(outputStream.toString(StandardCharsets.UTF_8), exec.exitStatus)
        }
    }

    @RefreshState
    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        val channel = session.createExecChannel(command.joinToOptString(" "))
        val inputStream = PipedInputStream()
        val outputStream = PipedOutputStream(inputStream)
        channel.out = outputStream

        channel.isRedirectErrorStream = true
        val future = channel.open()
        while (!future.await(1, TimeUnit.SECONDS)) {
            ProgressManager.checkCanceled()
        }

        return SshInteractiveShell(channel, inputStream, channel.invertedIn)
    }

    override fun getOS(): OS {
        return connectConfig.os
    }

    @RefreshState
    override fun transferFile(src: String, dest: String, indicator: ProgressIndicator?) {
        val file = File(src)
        if (file.length() == 0L) {
            return
        }
        logger.info("Uploading $src to $dest")
        val total = file.length().toDouble()
        val totalMb = String.format("%.2f", total / 1024 / 1024)
        var written = 0L

        indicator?.let {
            it.pushState()
            it.text = "Uploading ${file.name} to $dest"
        }

        try {
            SftpClientFactory.instance().createSftpClient(session).use { client ->
                client.open(dest, listOf(SftpClient.OpenMode.Write, SftpClient.OpenMode.Create)).use { handle ->
                    file.inputStream().use { input ->
                        val buf = ByteArray((file.length() / 2).coerceAtMost(5 * 1024 * 1024).toInt())
                        var len: Int
                        while (input.read(buf).also { len = it } != -1) {
                            ProgressManager.checkCanceled()
                            client.write(handle, written, buf, 0, len)
                            indicator ?.let {
                                it.fraction = written / total
                                it.text = "Uploading ${file.name} to $dest (${
                                    String.format(
                                        "%.2f",
                                        written.toDouble() / 1024 / 1024
                                    )
                                }MB / ${totalMb}MB)"
                            }
                            written += len
                        }
                    }
                }
            }
        } finally {
            indicator?.popState()
        }
    }


    override fun getConfiguration(): SshHostMachineConnectConfig {
        return connectConfig
    }


    override fun toString(): String {
        return "RemoteSshHostMachineImpl(name = ${config.name}, host=${connectConfig.ssh.host}, port=${connectConfig.ssh.port})"
    }


    override fun getHostMachineConfig(): HostMachineConfig {
        return config
    }


}