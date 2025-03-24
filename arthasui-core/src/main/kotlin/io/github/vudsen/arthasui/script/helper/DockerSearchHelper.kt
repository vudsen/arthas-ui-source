package io.github.vudsen.arthasui.script.helper

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.util.BridgeUtils
import io.github.vudsen.arthasui.bridge.util.ok
import io.github.vudsen.arthasui.common.util.ListMapTypeToken
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService

class DockerSearchHelper(private val hostMachine: HostMachine) {

    companion object {
        const val SEARCH_IMAGE_AND_NAME = "docker ps --format '{\\\"Names\\\": \\\"{{ .Names }}, \\\"Image\\\": \\\"{{ .Image }}\\\"}'"
    }

    @Suppress("unused")
    fun findByImage(image: String): List<JVM> {
        val output = "[" + BridgeUtils.grep(
            hostMachine,
            SEARCH_IMAGE_AND_NAME,
            "\"Image\": \"${image}\""
        ) + "]"
        val gson = service<SingletonInstanceHolderService>().gson
        return gson.fromJson(output, ListMapTypeToken()).map { ele ->
            return@map DockerJvm(ele["Names"]!!, ele["Names"]!!)
        }
    }


    @Suppress("unused")
    fun findByImageAndNamePrefix(image: String, prefix: String): List<JVM> {
        val output = when (hostMachine.getOS()) {
            OS.WINDOWS -> {
                hostMachine.execute("cmd", "/c", "\"$SEARCH_IMAGE_AND_NAME | grep $image | grep ${prefix}\"")
            }
            OS.LINUX -> {
                hostMachine.execute("sh", "-c", "\"$SEARCH_IMAGE_AND_NAME | findstr $image | findstr ${prefix}\"")
            }
            OS.MAC -> {
                TODO("Support MacOS")
            }
        }.ok()
        val gson = service<SingletonInstanceHolderService>().gson
        return gson.fromJson(output, ListMapTypeToken()).map { ele ->
            return@map DockerJvm(ele["Names"]!!, ele["Names"]!!)
        }
    }

}