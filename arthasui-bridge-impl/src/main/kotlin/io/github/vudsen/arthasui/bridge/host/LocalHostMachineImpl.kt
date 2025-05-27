package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.currentOS
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.bridge.bean.StandardInteractiveShell
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


/**
 * 针对本地宿主机的实现
 *
 * 由于服务器基本都是 Linux，所以没有必要再给 Windows 和 MacOS 上维护代码了。对于这俩强制要求只能连接本地的 JVM，不提供远程连接的实现。
 */
class LocalHostMachineImpl(
    private val config: HostMachineConfig
) : ShellAvailableHostMachine {

    private val connectConfig = config.connect as LocalConnectConfig

    private val os = currentOS()

    companion object {
        private val logger = Logger.getInstance(LocalHostMachineImpl::class.java.name)
    }

    override fun execute(vararg command: String): CommandExecuteResult {
        val process = try {
            ProcessBuilder(*command).redirectErrorStream(true).start()
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                logger.error("Failed to execute command: $command", e)
            }
            return CommandExecuteResult(e.message ?: "<Unknown>", 1)
        }
        val baos = ByteArrayOutputStream(512)
        val buf = ByteArray(512)
        while (true) {
            ProgressManager.checkCanceled()
            if (process.inputStream.read(buf).also { readBytes ->
                    if (readBytes == -1) {
                        return@also
                    }
                    baos.write(buf, 0, readBytes)
                } == -1 && process.waitFor(1, TimeUnit.SECONDS)) {
                break
            }
        }
        // Ensure all remaining data is read
        while (process.inputStream.read(buf).also { readBytes ->
                if (readBytes == -1) return@also
                baos.write(buf, 0, readBytes)
            } != -1) {
            ProgressManager.checkCanceled()
        }
        return CommandExecuteResult(baos.toString(), process.exitValue())
    }

    override fun createInteractiveShell(vararg command: String): InteractiveShell {
        val process = ProcessBuilder(*command).redirectErrorStream(true).start()
        return StandardInteractiveShell(process)
    }

    override fun getOS(): OS {
        return os
    }

    override fun transferFile(src: String, dest: String, indicator: ProgressIndicator?) {
        val actualDest = if (File(dest).isDirectory) {
            val name = File(src).name
            "$dest/$name"
        } else {
            dest
        }
        FileInputStream(src).channel.use { ins ->
            FileOutputStream(actualDest).channel.use { out ->
                out.transferFrom(ins, 0, ins.size())
            }
        }
    }



    override fun getConfiguration(): HostMachineConnectConfig {
        return connectConfig
    }

    override fun isArm(): Boolean {
        return false
    }

    override fun isFileNotExist(path: String): Boolean {
        return File(path).exists()
    }

    override fun isDirectoryExist(path: String): Boolean {
        return File(path).isDirectory
    }

    override fun mkdirs(path: String) {
        File(path).mkdirs()
    }

    override fun listFiles(directory: String): List<String> {
        return File(directory).listFiles()?.map { v -> v.name } ?: emptyList()
    }

    override fun download(url: String, destPath: String) {
        val destFile = File(destPath)
        if (destFile.exists() && destFile.isFile) {
            return
        }
        if (!destFile.parentFile.mkdirs()) {
            logger.warn("Failed to create directory for file: $destPath")
        }

        val tempFile = File(destFile.parentFile.absolutePath + "/" + destFile.name + ".tmp")

        val progressIndicator = getUserData(HostMachine.PROGRESS_INDICATOR)?.get()
        val baseText = "Downloading $url"
        progressIndicator ?.let {
            it.pushState()
            it.text = baseText
        }

        try {
            tempFile.outputStream().use { output ->
                val connection = URL(url).openConnection() as HttpURLConnection
                try {
                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        throw IllegalStateException(
                            "Unexpected HTTP status ${connection.responseCode}, body: ${
                                String(
                                    connection.inputStream.readAllBytes(),
                                    StandardCharsets.UTF_8
                                )
                            }"
                        )
                    }
                    val total = connection.contentLength
                    val totalMb = String.format("%.2f", total * 1.0 / 1024 / 1024)

                    connection.inputStream.use { input ->
                        val buffer = ByteArray(connection.contentLength.coerceAtMost(1024 * 1024 * 10))
                        var bytesRead: Int
                        var totalBytesRead = 0
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            ProgressManager.checkCanceled()
                            progressIndicator ?.let {
                                it.fraction = totalBytesRead.toDouble() / total
                                it.text = "$baseText (${String.format("%.2fMB", totalBytesRead * 1.0 / 1024 / 1024)} / ${totalMb}MB)"
                            }
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            }
            tempFile.renameTo(destFile)
        } finally {
            progressIndicator?.popState()
        }
    }

    override fun tryUnzip(target: String, destDir: String): Boolean {
        val file = File(target)
        if (!File(destDir).mkdirs()) {
            logger.warn("Failed to create directory $destDir")
        }
        if (file.extension == "zip") {
            java.util.zip.ZipFile(file).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val entryDest = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        entryDest.mkdirs()
                    } else {
                        entryDest.outputStream().use { output ->
                            zip.getInputStream(entry).use { input ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
            return true
        } else if (file.extension == "tgz" || file.extension == "tar.gz") {
            java.util.zip.GZIPInputStream(file.inputStream()).use { gzipInput ->
                TarArchiveInputStream(gzipInput).use { tarInput ->
                    var entry = tarInput.nextEntry
                    while (entry != null) {
                        val entryDest = File(destDir, entry.name)
                        if (entry.isDirectory) {
                            entryDest.mkdirs()
                        } else {
                            entryDest.outputStream().use { output ->
                                tarInput.copyTo(output)
                            }
                        }
                        entry = tarInput.nextEntry
                    }
                }
            }
            return true
        }
        return false
    }

    override fun grep(
        search: String,
        vararg commands: String
    ): CommandExecuteResult {
        // 本地就不考虑网络占用了
        val result = execute(*commands)
        if (result.exitCode != 0) {
            return result
        }
        return CommandExecuteResult(result.stdout.split('\n').filter { it.contains(search) }.joinToString("\n").trim(), 0)
    }

    override fun grep(
        searchChain: Array<String>,
        vararg commands: String
    ): CommandExecuteResult {
        val result = execute(*commands)
        if (result.exitCode != 0) {
            return result
        }
        val strings = result.stdout.split('\n')
        val notAvailable = arrayOfNulls<Boolean>(strings.size)

        for (s in searchChain) {
            for ((index, row) in strings.withIndex()) {
                if (notAvailable[index] == true) {
                    continue
                }
                if (!row.contains(s)) {
                    notAvailable[index] = true
                }
            }
        }
        val actualResult = StringBuilder()
        for (i in strings.indices) {
            if (notAvailable[i] == true) {
                continue
            }
            actualResult.append(strings[i].trim()).append('\n')
        }

        if (actualResult.isNotEmpty()) {
            actualResult.deleteCharAt(actualResult.length - 1)
        }
        return CommandExecuteResult(actualResult.toString(), 0)
    }

    override fun env(name: String): String? {
        return System.getenv(name)
    }

    override fun test() {}

    override fun getHostMachine(): HostMachine {
        return this
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return config
    }

    override fun resolveDefaultDataDirectory(): String {
        val home = System.getProperty("user.home")
        val dest = if (currentOS() == OS.MAC) {
            "$home/Library/Application Support/arthas-ui"
        } else if (currentOS() == OS.WINDOWS) {
            "$home\\AppData\\Local\\arthas-ui"
        } else {
            "/opt/arthas-ui"
        }
        return (File(dest)).absolutePath
    }

    private fun mv0(src: File, dest: File) {
        if (!src.exists()) {
            return
        }

        if (src.isDirectory) {
            if (!dest.exists()) {
                dest.mkdirs()
            }

            src.listFiles()?.forEach { file ->
                val target = File(dest, file.name)
                mv0(file, target)
            }

            src.delete()
        } else {
            src.renameTo(dest).takeIf { it } ?: run {
                // renameTo 失败，尝试复制再删除
                src.copyTo(dest, overwrite = true)
                src.delete()
            }
        }
    }

    override fun mv(src: String, dest: String, recursive: Boolean) {
        mv0(File(src), File(dest))
    }

    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }

}