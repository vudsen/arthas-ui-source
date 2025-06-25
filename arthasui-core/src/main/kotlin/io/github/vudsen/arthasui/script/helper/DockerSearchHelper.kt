package io.github.vudsen.arthasui.script.helper

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.providers.DockerJvmProvider

/**
 * A helper for docker container search
 */
class DockerSearchHelper(template: HostMachine, providerConfig: JvmInDockerProviderConfig) {

    companion object {
        private val SEARCH_IMAGE_AND_NAME = arrayOf("docker", "ps", "--format", "\"{\\\"Names\\\": \\\"{{ .Names }}\\\", \\\"ID\\\": \\\"{{ .ID }}\\\", \\\"Image\\\": \\\"{{ .Image }}\\\"}\"")
    }

    private val ctx = JvmContext(template, providerConfig)

    /**
     * Find the jvm by the image.
     * @param image The image name
     * @param name Customize the jvm name. Will use the container name if it's null.
     */
    @Suppress("unused")
    fun findByImage(image: String, name: String?): List<JVM> {
        val output = (ctx.hostMachine as ShellAvailableHostMachine).grep(
            "\"Image\": \"${image}\"",
            *SEARCH_IMAGE_AND_NAME,
        ).ok()

        val parseOutput = DockerJvmProvider.parseOutput(output, ctx)
        name ?.let {
            for (jvm in parseOutput) {
                jvm.name = it
            }
        }
        return parseOutput
    }


    /**
     * Find the jvm by image and name prefix.
     * @param image The image name.
     * @param prefix The name prefix.
     * @param name Customize the jvm name. Will use the container name if it's null.
     */
    @Suppress("unused")
    fun findByImageAndNamePrefix(image: String, prefix: String, name: String?): List<JVM> {
        val output = (ctx.hostMachine as ShellAvailableHostMachine).grep(arrayOf(image, prefix), *SEARCH_IMAGE_AND_NAME).ok()

        val parseOutput = DockerJvmProvider.parseOutput(output, ctx)
        name ?.let {
            for (jvm in parseOutput) {
                jvm.name = it
            }
        }
        return parseOutput
    }


}