package io.github.vudsen.arthasui.api.template

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import java.lang.ref.WeakReference

interface HostMachineTemplate : UserDataHolder {

    companion object {
        /**
         * 进度指示器. 在调用 [HostMachineTemplate.download] 或其它方法之前设置该属性，实现类就可以通过该对象反馈进度.
         */
        val PROGRESS_INDICATOR = Key<WeakReference<ProgressIndicator>>("Download Indicator")
    }

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
     * 测试连接
     */
    fun test()

    /**
     * 获取宿主机
     */
    fun getHostMachine(): HostMachine

    /**
     * 获取配置
     */
    fun getHostMachineConfig(): HostMachineConfig

    /**
     * 生成默认的数据文件夹
     */
    fun resolveDefaultDataDirectory(): String

}