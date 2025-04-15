package io.github.vudsen.arthasui.bridge.template

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import java.io.InputStream

class LinuxHostMachineTemplate(private val hostMachine: HostMachine, private val hostMachineConfig: HostMachineConfig) : HostMachineTemplate {

    override fun isArm(): Boolean {
        val result = hostMachine.execute("uname", "-a").ok()
        return result.contains("arm", ignoreCase = true) ||
                result.contains("aarch64", ignoreCase = true) ||
                result.contains("arm64", ignoreCase = true)
    }

    override fun isFileNotExist(path: String): Boolean {
        return hostMachine.execute("test", "-d", path).exitCode != 0
    }

    override fun mkdirs(path: String) {
        hostMachine.execute("mkdir", "-r", path)
    }

    private fun handleCurlOutput(progressIndicator: ProgressIndicator, inputStream: InputStream) {
        TODO()
    }

    private fun handleWgetOutput(progressIndicator: ProgressIndicator, inputStream: InputStream) {
        TODO()
    }

    override fun download(url: String, destDir: String) {
        val progressIndicator = getUserData(HostMachineTemplate.DOWNLOAD_PROGRESS_INDICATOR)?.get()
        if (hostMachine.execute("curl", "--version").exitCode == 0) {
            if (progressIndicator == null) {
                hostMachine.execute("curl", "-L", "-o", destDir, url).ok()
            } else {
                val shell =
                    hostMachine.createInteractiveShell("curl", "--progress-bar", "-L", "-o", destDir, url)
                handleCurlOutput(progressIndicator, shell.inputStream)
            }
            return
        }
        if (hostMachine.execute("wget", "--version").exitCode == 0) {
            // TODO, support progressIndicator
            if (progressIndicator == null) {
                hostMachine.execute("wget", "-O", destDir, url).ok()
            } else {
                val shell =
                    hostMachine.createInteractiveShell("wget", "-O", destDir, url)
                handleWgetOutput(progressIndicator, shell.inputStream)
            }
            return
        }
        throw IllegalStateException("No download toolchain available! Please consider install 'curl' or 'wget', or you can enable the 'Transfer From Local'")
    }

    override fun unzip(target: String) {
        if (target.endsWith(".zip")) {
            hostMachine.execute("unzip", target).ok()
        } else if (target.endsWith(".tgz")) {
            hostMachine.execute("tar", "-zxvf", target).ok()
        }
    }

    override fun grep(source: String, search: String): String {
        return hostMachine.execute("sh", "-c", "\"$source | grep ${search}\"").ok()
    }

    override fun env(name: String): String {
        return hostMachine.execute("echo", "$${name}").ok()
    }

    override fun test(): Boolean {
        return true
    }

    override fun getHostMachine(): HostMachine {
        return hostMachine
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return hostMachineConfig
    }

    override fun generateDefaultDataDirectory(): String {
        return "/opt/arthas"
    }

    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }

}