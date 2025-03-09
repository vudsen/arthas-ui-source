package io.github.vudsen.arthasui.bridge.bean

data class SshConfiguration(
    var host: String = "",
    var port: Int = 22,
    var username: String = "",
    var password: String = ""
)
