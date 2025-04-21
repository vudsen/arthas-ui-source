package io.github.vudsen.arthasui.bridge.template

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import java.io.BufferedReader
import java.io.InputStream

class LinuxHostMachineTemplate(private val hostMachine: HostMachine, private val hostMachineConfig: HostMachineConfig) : HostMachineTemplate {

    companion object {
        private val logger = Logger.getInstance(LinuxHostMachineTemplate::class.java)
    }

    override fun isArm(): Boolean {
        val result = hostMachine.execute("uname", "-a").ok()
        return result.contains("arm", ignoreCase = true) ||
                result.contains("aarch64", ignoreCase = true) ||
                result.contains("arm64", ignoreCase = true)
    }

    override fun isFileNotExist(path: String): Boolean {
        return hostMachine.execute("test", "-f", path).exitCode != 0
    }

    override fun isDirectoryExist(path: String): Boolean {
        return hostMachine.execute("test", "-d", path).exitCode == 0
    }

    override fun mkdirs(path: String) {
        hostMachine.execute("mkdir", "-p", path).ok()
    }


    private fun handleDownloadOutput(progressIndicator: ProgressIndicator, inputStream: InputStream) {
        BufferedReader(inputStream.reader()).use { br ->
            var line = ""
            while (br.readLine().also { line = it } != null) {
                ProgressManager.checkCanceled()
                if (line.contains("%")) {
                    val percentStr = line.substringBefore("%").trim()
                    if (percentStr.isEmpty()) {
                        progressIndicator.fraction = 0.0
                        continue
                    }
                    try {
                        progressIndicator.fraction = percentStr.toDouble()
                    } catch (e: NumberFormatException) {
                        logger.error("Failed to parse progress: $percentStr", e)
                    }
                }
            }
        }
    }


    override fun download(url: String, destPath: String) {
        if (!isFileNotExist(destPath)) {
            return
        }
        val progressIndicator = getUserData(HostMachineTemplate.DOWNLOAD_PROGRESS_INDICATOR)?.get()
        if (hostMachine.execute("curl", "--version").exitCode == 0) {
            if (progressIndicator == null) {
                hostMachine.execute("curl", "-L", "-o", destPath, url).ok()
            } else {
                progressIndicator.text = "Downloading $url"
                val shell =
                    hostMachine.createInteractiveShell("curl", "--progress-bar", "-L", "-o", destPath, "--connect-timeout", "10", url)
                handleDownloadOutput(progressIndicator, shell.inputStream)
            }
            return
        }
        if (hostMachine.execute("wget", "--version").exitCode == 0) {
            if (progressIndicator == null) {
                hostMachine.execute("wget", "-O", destPath, url).ok()
            } else {
                progressIndicator.text = "Downloading $url"
                val shell =
                    hostMachine.createInteractiveShell("wget", "-O", destPath, "--timeout=10", url)
                handleDownloadOutput(progressIndicator, shell.inputStream)
            }
            return
        }
        throw IllegalStateException("No download toolchain available! Please consider install 'curl' or 'wget', or you can enable the 'Transfer From Local'")
    }

    override fun unzip(target: String, destDir: String) {
        if (target.endsWith(".zip")) {
            hostMachine.execute("unzip", target, "-d", destDir).ok()
        } else if (target.endsWith(".tgz")) {
            hostMachine.execute("tar", "-zxvf", target, "-C", destDir).ok()
        }
    }


    override fun grep(source: String, search: String): String {
        return hostMachine.execute("sh", "-c", "\"$source | grep ${search}\"").ok()
    }

    override fun env(name: String): String {
        return hostMachine.execute("bash", "-lc", "'echo \$$name'").ok().trim()
    }

    override fun test() {
        hostMachine.execute("uname", "-a").ok()
    }

    override fun getHostMachine(): HostMachine {
        return hostMachine
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return hostMachineConfig
    }

    override fun generateDefaultDataDirectory(): String {
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