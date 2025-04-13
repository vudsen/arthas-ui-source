package io.github.vudsen.arthasui.bridge.toolchain

interface ToolchainManager {

    /**
     * 确保所有工具链下载完毕
     */
    suspend fun ensureToolChainDownloaded()

    /**
     * 获取工具链的路径
     */
    fun getToolChainPath(toolChain: ToolChain): String

}