package io.github.vudsen.arthasui.api.toolchain

/**
 * 管理文件的下载
 */
interface ToolchainManager {


    /**
     * 获取工具链的路径. 如果工具链不存在，则会进行下载并解压
     */
    fun getToolChainHomePath(toolChain: ToolChain): String

    /**
     * 初始化所有必要的目录
     */
    fun initDirectories()

}