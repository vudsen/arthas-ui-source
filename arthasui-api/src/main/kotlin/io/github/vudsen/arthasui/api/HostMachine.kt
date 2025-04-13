package io.github.vudsen.arthasui.api

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
     * 创建一个交互式的连接
     */
    fun createInteractiveShell(vararg command: String): InteractiveShell

    /**
     * 获取操作系统类型
     */
    fun getOS(): OS

    /**
     * 将本地的文件发送到宿主机上面
     * @param src 文件路径
     * @param dest 目标路径，可以是文件夹或者具体的名称
     */
    fun transferFile(src: String, dest: String)

    /**
     * 准备数据文件夹，可以被多次调用，每次将会返回想同的值
     * @return 存放数据的文件
     */
    fun prepareDataDirectory(): String

    /**
     * 获取连接配置
     */
    fun getConfiguration(): HostMachineConnectConfig


}