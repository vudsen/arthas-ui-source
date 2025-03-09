package io.github.vudsen.arthasui.api

import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell


interface CommandExecutor {

    /**
     * 执行一条命令
     */
    fun execute(command: String): CommandExecuteResult

    /**
     * 创建一个交互式的连接
     */
    fun createInteractiveShell(command: String): InteractiveShell


}