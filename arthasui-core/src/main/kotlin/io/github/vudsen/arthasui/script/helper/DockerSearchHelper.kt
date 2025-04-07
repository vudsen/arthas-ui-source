package io.github.vudsen.arthasui.script.helper

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.util.grep
import io.github.vudsen.arthasui.common.util.ListMapTypeToken
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService

class DockerSearchHelper(hostMachine: HostMachine, providerConfig: JvmInDockerProviderConfig) {

    companion object {
        const val SEARCH_IMAGE_AND_NAME = "docker ps --format '{\\\"Names\\\": \\\"{{ .Names }}, \\\"Image\\\": \\\"{{ .Image }}\\\"}'"
    }

    private val ctx = JvmContext(hostMachine, providerConfig)

    @Suppress("unused")
    fun findByImage(image: String, name: String?): List<JVM> {
        val output = "[" + ctx.hostMachine.grep(
            SEARCH_IMAGE_AND_NAME,
            "\"Image\": \"${image}\""
        ) + "]"
        val gson = service<SingletonInstanceHolderService>().gson
        return gson.fromJson(output, ListMapTypeToken()).map { ele ->
            return@map DockerJvm(ele["Names"]!!, name ?: ele["Names"]!!, ctx)
        }
    }


    @Suppress("unused")
    fun findByImageAndNamePrefix(image: String, prefix: String, name: String?): List<JVM> {
        val output = when (ctx.hostMachine.getOS()) {
            OS.WINDOWS -> {
                ctx.hostMachine.execute("cmd", "/c", "\"$SEARCH_IMAGE_AND_NAME | grep $image | grep ${prefix}\"")
            }
            OS.LINUX -> {
                ctx.hostMachine.execute("sh", "-c", "\"$SEARCH_IMAGE_AND_NAME | findstr $image | findstr ${prefix}\"")
            }
            OS.MAC -> {
                TODO("Support MacOS")
            }
        }.ok()
        val gson = service<SingletonInstanceHolderService>().gson
        return gson.fromJson(output, ListMapTypeToken()).map { ele ->
            return@map DockerJvm(ele["Names"]!!, name ?: ele["Names"]!!, ctx)
        }
    }

}