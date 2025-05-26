package io.github.vudsen.arthasui.api.toolchain

enum class ToolChain {
    JATTACH_BUNDLE,
    /**
     * 获取 arthas-bin.zip
     */
    ARTHAS_BUNDLE,
    /**
     * 该工具链直接执行可执行文件
     */
    KUBECTL
}