package io.github.vudsen.arthasui.bridge.template

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.template.HostMachineTemplate

class LinuxHostMachineTemplate(private val hostMachine: HostMachine) : HostMachineTemplate {


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

    override fun download(url: String, destDir: String) {
        if (hostMachine.execute("curl", "--version").exitCode == 0) {
            hostMachine.execute("curl", "-o", destDir, url).ok()
            return
        }
        if (hostMachine.execute("wget", "--version").exitCode == 0) {
            hostMachine.execute("wget", "-O", destDir, url).ok()
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

}