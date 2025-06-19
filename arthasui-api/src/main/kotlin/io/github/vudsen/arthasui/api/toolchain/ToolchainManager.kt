package io.github.vudsen.arthasui.api.toolchain

/**
 * 管理文件的下载
 */
interface ToolchainManager {


    /**
     * 获取工具链的路径. 如果工具链不存在，则会进行下载并解压
     * @param toolChain 工具链
     * @param version 版本号，如果为空，使用最新稳定版本
     */
    fun getToolChainHomePath(toolChain: ToolChain, version: String? = null): String

}