package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.ui.DockerJvmProviderForm
import io.github.vudsen.arthasui.bridge.util.InteractiveShell2ArthasProcessAdapter
import io.github.vudsen.arthasui.common.util.ListMapTypeToken
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService

class DockerJvmProvider : JvmProvider {
    override fun getName(): String {
        return "Docker"
    }

    override fun searchJvm(template: HostMachineTemplate, providerConfig: JvmProviderConfig): List<JVM> {
        val hostMachine = template.getHostMachine()
        val gson = service<SingletonInstanceHolderService>().gson
        val config = providerConfig as JvmInDockerProviderConfig
        val execResult = hostMachine.execute(config.dockerPath, "ps", "--format=json")
        if (execResult.exitCode != 0) {
            TODO("handle non-zero exit code.")
        }
        val jsonArray = "[" + execResult.stdout + "]"
        val tree = gson.fromJson(jsonArray, ListMapTypeToken())
        val result = mutableListOf<JVM>()
        for (element in tree) {
            result.add(DockerJvm(
                element["ID"]!!,
                "${element["Names"]!!}(${element["Image"]!!})",
                JvmContext(template, providerConfig))
            )
        }
        return result
    }

    override fun createArthasBridgeFactory(
        template: HostMachineTemplate,
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        val config = jvmProviderConfig as JvmInDockerProviderConfig
        val hostMachine = template.getHostMachine()
        if (hostMachine.getOS() == OS.WINDOWS && !config.useToolsInContainer) {
            throw IllegalStateException("Docker desktop is not supported. Please embed your jdk and arthas to your image and enable `useToolsInContainer` feature.")
        }
        return ArthasBridgeFactory {
            val jdkHome: String
            val arthasHome: String
            if (config.useToolsInContainer) {
                jdkHome = config.jdkHome
                arthasHome = config.arthasHome
            } else {
                hostMachine.execute("docker", "cp", config.arthasHome, "${jvm.id}:/tmp/arthas").ok()
                hostMachine.execute("docker", "cp", config.jdkHome, "${jvm.id}:/tmp/jdk").ok()
                jdkHome = "/tmp/jdk"
                arthasHome = "/tmp/arthas"
            }
            return@ArthasBridgeFactory ArthasBridgeImpl(
                InteractiveShell2ArthasProcessAdapter(
                // TODO, support switch pid.
                    hostMachine.createInteractiveShell("docker", "exec", "-it",
                    jvm.id, "$jdkHome/bin/java", "-jar", "${arthasHome}/arthas-boot.jar", "1"),
            )
            )
        }
    }

    override fun createForm(oldState: JvmProviderConfig?, parentDisposable: Disposable): FormComponent<JvmProviderConfig> {
        return DockerJvmProviderForm(oldState, parentDisposable)
    }

    override fun getConfigClass(): Class<out JvmProviderConfig> {
        return JvmInDockerProviderConfig::class.java
    }

    override fun getJvmClass(): Class<out JVM> {
        return DockerJvm::class.java
    }

    override fun isJvmInactive(jvm: JVM): Boolean {
        val ctx = jvm.context
        val config = ctx.providerConfig as JvmInDockerProviderConfig
        val execResult = ctx.template.getHostMachine().execute(config.dockerPath, "ps", "--format=json", "--filter", "id=${jvm.id}").ok()
        return execResult.isEmpty()
    }

    override fun tryCreateDefaultConfiguration(template: HostMachineTemplate): JvmProviderConfig {
        val result = template.getHostMachine().execute("docker", "version")
        if (result.exitCode != 0) {
            return JvmInDockerProviderConfig()
        }
        return JvmInDockerProviderConfig(true)
    }


}