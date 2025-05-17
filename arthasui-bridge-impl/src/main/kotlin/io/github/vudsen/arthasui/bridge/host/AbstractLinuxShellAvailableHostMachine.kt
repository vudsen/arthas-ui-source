package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.bridge.host.SshLinuxHostMachineImpl.Companion.logger
import io.github.vudsen.arthasui.bridge.util.RefreshState
import java.io.BufferedReader

abstract class AbstractLinuxShellAvailableHostMachine : ShellAvailableHostMachine {

    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }

    override fun resolveDefaultDataDirectory(): String {
        return "/opt/arthas-ui"
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
                return it.exitCode == 0
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

}