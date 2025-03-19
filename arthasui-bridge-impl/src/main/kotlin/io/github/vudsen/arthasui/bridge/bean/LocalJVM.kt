package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM


class LocalJVM(val myPid: String, private val mainClass: String) : JVM {

    override fun getDisplayName(): String {
        return "Pid = $myPid, MainClass = $mainClass"
    }

    override fun getMainClass(): String {
        return mainClass
    }


    override fun getId(): String {
        return myPid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalJVM

        if (myPid != other.myPid) return false
        if (mainClass != other.mainClass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = myPid.hashCode()
        result = 31 * result + mainClass.hashCode()
        return result
    }

    override fun toString(): String {
        return "type = local, ${getDisplayName()}"
    }

}