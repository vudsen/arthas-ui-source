package io.github.vudsen.arthasui.api.exception

/**
 * 应用异常, **通常是预期内的异常**
 */
class AppException(msg: String, cause: Exception? = null) : Exception(msg, cause, false, false) {

    constructor(e: Exception) : this(e.message ?: "<Unknown>", e)
}