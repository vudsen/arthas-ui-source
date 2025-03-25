package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM


class LocalJVM(pid: String, mainClass: String) : JVM(pid, mainClass) {

    override fun toString(): String {
        return "type = local, $name"
    }

}