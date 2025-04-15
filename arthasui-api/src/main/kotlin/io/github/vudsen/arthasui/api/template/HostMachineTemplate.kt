package io.github.vudsen.arthasui.api.template

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import java.lang.ref.WeakReference

interface HostMachineTemplate : UserDataHolder {

    companion object {
        /**
         * 下载进度指示器. 在调用 [HostMachineTemplate.download] 之前设置该属性，实现类就可以通过该对象反馈进度.
         */
        val DOWNLOAD_PROGRESS_INDICATOR = Key<WeakReference<ProgressIndicator>>("Download Indicator")
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
     * 递归创建文件夹
     */
    fun mkdirs(path: String)

    /**
     * 将指定文件下载到文件夹中
     */
    fun download(url: String, destDir: String)

    /**
     * 解压压缩包(zip, tar.gz, tgz...)
     */
    fun unzip(target: String)

    /**
     * 过滤输出
     * @param source 要执行的命令
     * @param search 要搜索的内容
     */
    fun grep(source: String, search: String): String

    /**
     * 获取环境变量
     */
    fun env(name: String): String?

    /**
     * 测试连接
     */
    fun test(): Boolean

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
    fun generateDefaultDataDirectory(): String

}