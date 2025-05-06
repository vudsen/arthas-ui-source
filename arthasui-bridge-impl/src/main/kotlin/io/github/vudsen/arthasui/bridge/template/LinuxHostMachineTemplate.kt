package io.github.vudsen.arthasui.bridge.template

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import java.io.BufferedReader

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
        hostMachine.execute("mkdir", "-p", path)
    }

    override fun listFiles(directory: String): List<String> {
        hostMachine.execute("ls", directory).tryUnwrap() ?.let {
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
        var isCancelled = false
        try {
            progressIndicator.text = "Downloading $url"
            BufferedReader(shell.getInputStream().reader()).use { br ->
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


    override fun download(url: String, destPath: String) {
        if (!isFileNotExist(destPath)) {
            return
        }
        val i = destPath.lastIndexOf('/')
        if (i < 0) {
            throw IllegalStateException("Please provide a absolute valid path")
        }
        val brokenFlagPath = destPath.substring(0, i + 1) + "DOWNLOADING_" + destPath.substring(i + 1)
        val progressIndicator = getUserData(HostMachineTemplate.PROGRESS_INDICATOR)?.get()


        logger.info("Downloading $url to $brokenFlagPath.")
        if (hostMachine.execute("curl", "--version").exitCode == 0) {
            logger.info("Using curl")
            if (progressIndicator == null) {
                hostMachine.execute("curl", "-L", "-o", brokenFlagPath, "--connect-timeout", "10", url).ok()
            } else {
                hostMachine.createInteractiveShell(
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
        } else if (hostMachine.execute("wget", "--version").exitCode == 0) {
            logger.info("Using wget")
            if (progressIndicator == null) {
                hostMachine.execute("wget", "-O", brokenFlagPath, "--timeout=10", url).ok()
            } else {
                hostMachine.createInteractiveShell("wget", "-O", brokenFlagPath, "--timeout=10", url).use { shell ->
                    handleDownloadOutput(url, progressIndicator, shell)
                }
            }
        } else {
            throw IllegalStateException("No download toolchain available! Please consider install 'curl' or 'wget', or you can enable the 'Transfer From Local'")
        }
        hostMachine.execute("mv", brokenFlagPath, destPath).ok()
    }


    override fun tryUnzip(target: String, destDir: String): Boolean {
        if (target.endsWith(".zip")) {
            hostMachine.execute("unzip", target, "-d", destDir).let {
                if (it.exitCode == 1) {
                    return false
                } else if (it.exitCode == 0) {
                    return true
                }
                // throw exp
                it.ok()
                return false
            }
        } else if (target.endsWith(".tgz") || target.endsWith(".tar.gz")) {
            hostMachine.execute("tar", "-zxvf", target, "-C", destDir).ok()
            return true
        } else if (target.endsWith(".tar")) {
            hostMachine.execute("tar", "-xvf", target, "-C", destDir).ok()
            return true
        }
        return false
    }


    override fun grep(search: String, vararg commands: String): CommandExecuteResult {
        return hostMachine.execute("sh", "-c", "'${commands.joinToString(" ")} | grep ${search}'")
    }

    override fun grep(searchChain: Array<String>, vararg commands: String): CommandExecuteResult {
        val command = StringBuilder(searchChain.size * 5)
        command.append('\'')
        for (part in commands) {
            command.append(part).append(' ')
        }
        for (search in searchChain) {
            command.append("| grep ").append(search).append(' ')
        }
        command.append('\'')
        return hostMachine.execute("sh", "-c", command.toString().trim())
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