package io.github.vudsen.arthasui.api

import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell


/**
 * 宿主机
 */
interface HostMachine {

    /**
     * 执行一条命令
     */
    fun execute(vararg command: String): CommandExecuteResult

    /**
     * 创建一个交互式的连接
     */
    fun createInteractiveShell(vararg command: String): InteractiveShell

    /**
     * 获取操作系统类型
     */
    fun getOS(): OS

}