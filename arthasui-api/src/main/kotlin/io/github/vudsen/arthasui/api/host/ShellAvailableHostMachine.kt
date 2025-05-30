package io.github.vudsen.arthasui.api.host

import ai.grazie.utils.data.ValueDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell

/**
 * 可以执行 shell 命令的宿主机
 */
interface ShellAvailableHostMachine : HostMachine {


    /**
     * 执行一条命令
     */
    fun execute(vararg command: String): CommandExecuteResult

    /**
     * 创建一个交互式的连接.
     *
     * 交互式进程**可能在命令执行失败时也不会抛出异常**，需要手动通过 [InteractiveShell.exitCode] 来判断命令是否正常退出，然后进行错误处理。
     */
    fun createInteractiveShell(vararg command: String): InteractiveShell

    /**
     * 将本地的文件发送到宿主机上面
     * @param src 本地文件路径
     * @param dest 目标路径，指定文件的绝对路径。 **需要确保父目录存在**
     */
    fun transferFile(src: String, dest: String, indicator: ProgressIndicator?)

    /**
     * 是否为 arm 架构
     */
    fun isArm(): Boolean

    /**
     * 文件是否存在
     */
    fun isFileNotExist(path: String): Boolean

    /**
     * 文件夹是否存在
     */
    fun isDirectoryExist(path: String): Boolean

    /**
     * 递归创建文件夹
     */
    fun mkdirs(path: String)

    /**
     * 列出所有文件
     */
    fun listFiles(directory: String): List<String>

    /**
     * 将指定文件下载到文件夹中
     * @param url 下载地址
     * @param destPath 存放路径，需要指定文件名并且还需要保证文件夹存在
     */
    fun download(url: String, destPath: String)

    /**
     * 尝试使用系统本地工具库来解压压缩包(zip, tar.gz, tgz...)
     * @return 返回 true 表示解压成功，返回 false 表示系统不存在对应的工具链。如果系统存在对应的工具链，但是解压报错了，将会直接抛出异常
     */
    fun tryUnzip(target: String, destDir: String): Boolean

    /**
     * 过滤输出
     * @param search 要搜索的内容
     * @param commands 要执行的命令
     */
    fun grep(search: String, vararg commands: String): CommandExecuteResult

    /**
     * 多次过滤
     * @param searchChain 要搜索的内容
     * @param commands 要执行的命令
     */
    fun grep(searchChain: Array<String>, vararg commands: String): CommandExecuteResult

    /**
     * 获取环境变量
     */
    fun env(name: String): String?


    /**
     * 获取宿主机
     */
    fun getHostMachine(): HostMachine


    /**
     * 生成默认的数据文件夹
     */
    fun resolveDefaultDataDirectory(): String

    /**
     * 移动文件
     */
    fun mv(src: String, dest: String, recursive: Boolean)

}