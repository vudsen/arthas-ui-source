package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM


class DockerJvm(private val containerId: String, private val displayName: String) : JVM {

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getMainClass(): String {
        return displayName
    }

    override fun getId(): String {
        return containerId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DockerJvm

        if (containerId != other.containerId) return false
        if (displayName != other.displayName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerId.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }

    override fun toString(): String {
        return "type: docker, ${getDisplayName()}"
    }

}