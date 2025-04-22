package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.ui.DockerJvmProviderForm
import io.github.vudsen.arthasui.bridge.util.InteractiveShell2ArthasProcessAdapter
import io.github.vudsen.arthasui.common.util.MapTypeToken
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService

class DockerJvmProvider : JvmProvider {
    override fun getName(): String {
        return "Docker"
    }

    override fun searchJvm(template: HostMachineTemplate, providerConfig: JvmProviderConfig): List<JVM> {
        val hostMachine = template.getHostMachine()
        val gson = service<SingletonInstanceHolderService>().gson
        val config = providerConfig as JvmInDockerProviderConfig
        val execResult = hostMachine.execute(config.dockerPath, "ps", "--format=json").ok()

        val containers = execResult.split('\n')
        val result = mutableListOf<JVM>()
        for (container in containers) {
            if (container.isEmpty()) {
                continue
            }
            val tree = gson.fromJson(container, MapTypeToken())
            result.add(DockerJvm(
                tree["ID"]!!,
                "${tree["Names"]!!}(${tree["Image"]!!})",
                JvmContext(template, providerConfig))
            )
        }
        return result
    }

    private fun isDirectoryNotExistInContainer(hostMachine: HostMachine, id: String, path: String): Boolean {
        return hostMachine.execute("docker", "exec", "-i", id, "test", "-d", path).exitCode != 0
    }

    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig,
        toolchainManager: ToolchainManager
    ): ArthasBridgeFactory {
        val template = jvm.context.template
        val config = jvmProviderConfig as JvmInDockerProviderConfig
        val hostMachine = template.getHostMachine()
        val javaExecutable = if (config.javaHome.isEmpty()) {
            "java"
        }  else {
            config.javaHome + "/bin/java"
        }

        return ArthasBridgeFactory {
            val jattach = "/tmp/jdk"
            val arthasHome = "/tmp/arthas"
            if (isDirectoryNotExistInContainer(hostMachine, jvm.id, arthasHome)) {
                hostMachine.execute("docker", "cp", toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE), "${jvm.id}:$arthasHome").ok()
            }
            if (isDirectoryNotExistInContainer(hostMachine, jvm.id, jattach)) {
                hostMachine.execute("docker", "cp", toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE), "${jvm.id}:$jattach").ok()
            }

            // TODO, support switch pid
            hostMachine.execute("docker", "exec", "-i",
                jvm.id, "$jattach/jattach", "1", "load", "instrument", "false", "\"${arthasHome}/arthas-agent.jar\"").ok()
            return@ArthasBridgeFactory ArthasBridgeImpl(
                InteractiveShell2ArthasProcessAdapter(
                    hostMachine.createInteractiveShell("docker", "exec", "-i",
                        jvm.id, javaExecutable, "-jar", "${arthasHome}/arthas-client.jar"),
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