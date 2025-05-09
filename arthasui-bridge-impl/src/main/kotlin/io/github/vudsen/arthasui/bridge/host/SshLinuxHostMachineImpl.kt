package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import com.jetbrains.rd.generator.nova.util.joinToOptString
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
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
import java.io.BufferedReader
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
) : CloseableHostMachine, ShellAvailableHostMachine {

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
                writer.close()
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

    @RefreshState
    override fun isArm(): Boolean {
        val result = execute("uname", "-a").ok()
        return result.contains("arm", ignoreCase = true) ||
                result.contains("aarch64", ignoreCase = true) ||
                result.contains("arm64", ignoreCase = true)
    }

    @RefreshState
    override fun isFileNotExist(path: String): Boolean {
        return execute("test", "-f", path).exitCode != 0
    }

    @RefreshState
    override fun isDirectoryExist(path: String): Boolean {
        return execute("test", "-d", path).exitCode == 0
    }

    @RefreshState
    override fun mkdirs(path: String) {
        execute("mkdir", "-p", path)
    }

    @RefreshState
    override fun listFiles(directory: String): List<String> {
        execute("ls", directory).tryUnwrap() ?.let {
            val result = mutableListOf<String>()
            val buf = StringBuilder()
            for (ch in it) {
                if (ch == ' ' || ch == '\n') {
                    if (buf.isEmpty()) {
                        continue
                    }
                    result.add(buf.toString())
                    buf.clear()
                } else {
                    buf.append(ch)
                }
            }
            if (buf.isNotEmpty()) {
                result.add(buf.toString())
            }
            return result
        }
        return emptyList()
    }

    private fun handleDownloadOutput(url: String, progressIndicator: ProgressIndicator, shell: InteractiveShell) {
        progressIndicator.pushState()
        val lineTrace = arrayOfNulls<String>(5)
        var tp = 0
        try {
            progressIndicator.text = "Downloading $url"
            BufferedReader(shell.getReader()).use { br ->
                var line: String? = ""
                while (br.readLine().also { line = it } != null) {
                    ProgressManager.checkCanceled()
                    val currentLine = line!!
                    lineTrace[tp] = currentLine
                    tp = (tp + 1) % lineTrace.size
                    val i = currentLine.indexOf('%')
                    if (i < 0) {
                        continue
                    }
                    progressIndicator.text2 = currentLine
                    var len = 0
                    var sum = 0.0
                    var base = 1
                    for (pos in i - 1 downTo 0) {
                        val ch = currentLine[pos]
                        if (ch == '.') {
                            (0 until len).forEach { _ ->
                                sum *= 0.1
                                base = 1
                            }
                            continue
                        } else if (ch < '0' || ch > '9') {
                            break
                        }
                        len++
                        sum += (ch - '0') * base
                        base *= 10
                    }

                    if (sum > 0.0) {
                        progressIndicator.fraction = sum * 0.01
                    }
                }
            }
        } finally {
            progressIndicator.popState()
            shell.exitCode() ?.let {
                if (it != 0){
                    val stringBuilder = StringBuilder()
                    for (i in lineTrace.indices) {
                        val fp = (i - tp + lineTrace.size) % lineTrace.size
                        stringBuilder.append(lineTrace[fp]).append('\n')
                    }
                    throw IllegalStateException(stringBuilder.toString())
                }
            }
        }
    }


    @RefreshState
    override fun download(url: String, destPath: String) {
        if (!isFileNotExist(destPath)) {
            return
        }
        val i = destPath.lastIndexOf('/')
        if (i < 0) {
            throw IllegalStateException("Please provide a absolute valid path")
        }
        val brokenFlagPath = destPath.substring(0, i + 1) + "DOWNLOADING_" + destPath.substring(i + 1)
        val progressIndicator = getUserData(HostMachine.PROGRESS_INDICATOR)?.get()


        logger.info("Downloading $url to $brokenFlagPath.")
        if (execute("curl", "--version").exitCode == 0) {
            logger.info("Using curl")
            if (progressIndicator == null) {
                execute("curl", "-L", "-o", brokenFlagPath, "--connect-timeout", "10", url).ok()
            } else {
                createInteractiveShell(
                    "curl",
                    "--progress-bar",
                    "-L",
                    "-o",
                    brokenFlagPath,
                    "--connect-timeout",
                    "10",
                    url
                ).use { shell ->
                    handleDownloadOutput(url, progressIndicator, shell)
                }

            }
        } else if (execute("wget", "--version").exitCode == 0) {
            logger.info("Using wget")
            if (progressIndicator == null) {
                execute("wget", "-O", brokenFlagPath, "--timeout=10", url).ok()
            } else {
                createInteractiveShell("wget", "-O", brokenFlagPath, "--timeout=10", url).use { shell ->
                    handleDownloadOutput(url, progressIndicator, shell)
                }
            }
        } else {
            throw IllegalStateException("No download toolchain available! Please consider install 'curl' or 'wget', or you can enable the 'Transfer From Local'")
        }
        execute("mv", brokenFlagPath, destPath).ok()
    }

    @RefreshState
    override fun tryUnzip(target: String, destDir: String): Boolean {
        if (target.endsWith(".zip")) {
            execute("unzip", target, "-d", destDir).let {
                if (it.exitCode == 127) {
                    return false
                } else if (it.exitCode == 0) {
                    return true
                }
                // throw exp
                it.ok()
                return false
            }
        } else if (target.endsWith(".tgz") || target.endsWith(".tar.gz")) {
            execute("tar", "-zxvf", target, "-C", destDir).ok()
            return true
        } else if (target.endsWith(".tar")) {
            execute("tar", "-xvf", target, "-C", destDir).ok()
            return true
        }
        return false
    }

    @RefreshState
    override fun grep(
        search: String,
        vararg commands: String
    ): CommandExecuteResult {
        return execute("sh", "-c", "'${commands.joinToString(" ")} | grep ${search}'")
    }

    @RefreshState
    override fun grep(
        searchChain: Array<String>,
        vararg commands: String
    ): CommandExecuteResult {
        val command = StringBuilder(searchChain.size * 5)
        command.append('\'')
        for (part in commands) {
            command.append(part).append(' ')
        }
        for (search in searchChain) {
            command.append("| grep ").append(search).append(' ')
        }
        command.append('\'')
        return execute("sh", "-c", command.toString().trim())
    }

    @RefreshState
    override fun env(name: String): String? {
        return execute("bash", "-lc", "'echo \$$name'").ok().trim()
    }

    @RefreshState
    override fun test() {
        execute("uname", "-a").ok()
    }

    override fun getHostMachine(): HostMachine {
        return this
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return config
    }

    override fun resolveDefaultDataDirectory(): String {
        return "/opt/arthas-ui"
    }

    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }


}