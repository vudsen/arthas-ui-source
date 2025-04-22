package io.github.vudsen.arthasui.script.helper

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.common.util.ListMapTypeToken
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService

/**
 * A helper for docker container search
 */
class DockerSearchHelper(template: HostMachineTemplate, providerConfig: JvmInDockerProviderConfig) {

    companion object {
        private const val SEARCH_IMAGE_AND_NAME = "docker ps --format '{\\\"Names\\\": \\\"{{ .Names }}, \\\"Image\\\": \\\"{{ .Image }}\\\"}'"
    }

    private val ctx = JvmContext(template, providerConfig)

    /**
     * Find the jvm by the image.
     * @param image The image name
     * @param name Customize the jvm name. Will use the container name if it's null.
     */
    @Suppress("unused")
    fun findByImage(image: String, name: String?): List<JVM> {
        val output = "[" + ctx.template.grep(
            SEARCH_IMAGE_AND_NAME,
            "\"Image\": \"${image}\""
        ) + "]"
        val gson = service<SingletonInstanceHolderService>().gson
        return gson.fromJson(output, ListMapTypeToken()).map { ele ->
            return@map DockerJvm(ele["Names"]!!, name ?: ele["Names"]!!, ctx)
        }
    }


    /**
     * Find the jvm by image and name prefix.
     * @param image The image name.
     * @param prefix The name prefix.
     * @param name Customize the jvm name. Will use the container name if it's null.
     */
    @Suppress("unused")
    fun findByImageAndNamePrefix(image: String, prefix: String, name: String?): List<JVM> {
        val hostMachine = ctx.template.getHostMachine()
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
            return@map DockerJvm(ele["Names"]!!, name ?: ele["Names"]!!, ctx)
        }
    }

}