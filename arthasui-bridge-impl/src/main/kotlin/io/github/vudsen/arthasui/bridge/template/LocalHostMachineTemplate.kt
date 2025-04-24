package io.github.vudsen.arthasui.bridge.template

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.currentOS
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * 针对本地宿主机的实现
 *
 * 由于服务器基本都是 Linux，所以没有必要再给 Windows 和 MacOS 上维护代码了。对于这俩强制要求只能连接本地的 JVM，不提供远程连接的实现。
 */
class LocalHostMachineTemplate(private val hostMachine: HostMachine, private val hostMachineConfig: HostMachineConfig) : HostMachineTemplate {

    companion object {
        private val logger: Logger = Logger.getInstance(LocalHostMachineTemplate::class.java)
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
        return File(directory).listFiles().map { v -> v.name }
    }

    override fun download(url: String, destPath: String) {
        val destFile = File(destPath)
        if (destFile.exists() && destFile.isFile) {
            return
        }
        if (!destFile.parentFile.mkdirs()) {
            logger.warn("Failed to create directory for file: $destPath")
        }

        val progressIndicator = getUserData(HostMachineTemplate.PROGRESS_INDICATOR)?.get()
        progressIndicator?.text = "Downloading $url..."

        destFile.outputStream().use { output ->
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

                connection.inputStream.use { input ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var totalBytesRead = 0
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        progressIndicator?.fraction = totalBytesRead.toDouble() / total
                    }
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    override fun unzip(target: String, destDir: String) {
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
        } else if (file.extension == "tgz") {
            java.util.zip.GZIPInputStream(file.inputStream()).use { gzipInput ->
                TarArchiveInputStream(gzipInput).use { tarInput ->
                    var entry = tarInput.nextTarEntry
                    while (entry != null) {
                        val entryDest = File(destDir, entry.name)
                        if (entry.isDirectory) {
                            entryDest.mkdirs()
                        } else {
                            entryDest.outputStream().use { output ->
                                tarInput.copyTo(output)
                            }
                        }
                        entry = tarInput.nextTarEntry
                    }
                }
            }
        }
    }


    override fun grep(search: String, vararg commands: String): CommandExecuteResult {
        // 本地就不考虑网络占用了
        val result = hostMachine.execute(*commands)
        if (result.exitCode != 0) {
            return result
        }
        return CommandExecuteResult(result.stdout.split('\n').filter { it.contains(search) }.joinToString("\n").trim(), 0)
    }

    override fun grep(searchChain: Array<String>, vararg commands: String): CommandExecuteResult {
        val result = hostMachine.execute(*commands)
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
        return hostMachine
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return hostMachineConfig
    }

    override fun generateDefaultDataDirectory(): String {
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

    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }

}