package io.github.vudsen.arthasui.api

import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig


/**
 * 宿主机
 */
interface HostMachine {

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
     * 获取操作系统类型
     */
    fun getOS(): OS

    /**
     * 将本地的文件发送到宿主机上面
     * @param src 文件路径
     * @param dest 目标路径，指定文件绝对路径
     */
    fun transferFile(src: String, dest: String, indicator: ProgressIndicator?)


    /**
     * 获取连接配置
     */
    fun getConfiguration(): HostMachineConnectConfig


}