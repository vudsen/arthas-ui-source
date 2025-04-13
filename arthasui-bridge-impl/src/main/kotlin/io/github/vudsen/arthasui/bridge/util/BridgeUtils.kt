package io.github.vudsen.arthasui.bridge.util

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult

/**
 * grep 命令，兼容所有平台
 * @return 输出
 */
fun HostMachine.grep(source: String, search: String): String {
    val result: CommandExecuteResult =  when(getOS()) {
        OS.LINUX -> execute("sh", "-c", "\"$source | grep ${search}\"")
        OS.WINDOWS -> execute("cmd", "/c", "\"$source | findstr \"${search}\"\"")
        OS.MAC -> TODO("Support MacOS")
    }
    // grep 没找到会返回 1
    if (result.exitCode == 0) {
        return result.stdout
    }
    if (result.exitCode == 1 && result.stdout == "") {
        return ""
    }
    // throw error.
    throw IllegalStateException("Failed to execute script: ${result.stdout}")
}

/**
 * 获取环境变量
 */
fun HostMachine.env(name: String): String? {
    val result: String? =  when(getOS()) {
        OS.LINUX -> execute("echo", "$${name}").ok()
        OS.WINDOWS -> {
            val result = execute("cmd", "/c", "echo %${name}%").ok()
            if (result == "%${name}%") {
                null
            } else {
                result
            }
        }
        OS.MAC -> TODO("Support MacOS")
    }
    if (result.isNullOrEmpty()) {
        return null
    }
    return result
}

/**
 * 下载文件
 * @param url 文件的 url
 * @param dest 要下载到哪里，需要文件名
 */
fun HostMachine.download(url: String, dest: String) {
    if (execute("curl", "--version").exitCode == 0) {
        execute("curl", "-o", dest, url).ok()
        return
    }
    if (execute("wget", "--version").exitCode == 0) {
        execute("wget", "-O", dest, url).ok()
        return
    }
    throw IllegalStateException("No download toolchain available! Please consider install 'curl' or 'wget', or you can enable the 'Transfer From Local'")
}

/**
 * 测试连接并且返回系统版本
 * @return 系统版本
 */
fun HostMachine.test(): String {
    return when (getOS()) {
        OS.LINUX -> execute("uname", "-a").ok()
        OS.WINDOWS -> execute("cmd", "/c", "ver").ok()
//        OS.WINDOWS -> execute("ver").ok()
        OS.MAC -> execute("uname", "-a").ok()
    }
}

/**
 * 递归创建文件夹
 */
fun HostMachine.mkdirs(path: String) {
    when (getOS()) {
        OS.LINUX -> execute("mkdir", "-p", path).ok()
        OS.WINDOWS -> execute("cmd", "/c", "mkdir ${path}").ok()
        OS.MAC -> execute("mkdir", "-p", path).ok()
    }
}