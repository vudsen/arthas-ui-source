package io.github.vudsen.arthasui.api.exception

/**
 * 当命令执行失败时抛出
 */
class BridgeCommandFailedException(msg: String) : BridgeException(msg)