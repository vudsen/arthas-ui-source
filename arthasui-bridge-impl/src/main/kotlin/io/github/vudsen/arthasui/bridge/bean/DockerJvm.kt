package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM


class DockerJvm(containerId: String, displayName: String) : JVM(containerId, displayName) {


    override fun toString(): String {
        return "type: docker, $name"
    }

}