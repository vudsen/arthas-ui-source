package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.test.BridgeTestUtil
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.core.ArthasExecutionManagerImpl
import org.junit.Assert

/**
 * Test attach.
 *
 * For chinese developer, you can add environment variable `TOOLCHAIN_MIRROR=https://5j9g3t.site/github-mirror`
 * to avoid download failed from GitHub.
 */
class ArthasBridgeImplTest : BasePlatformTestCase() {


    fun testAttachLinuxLocal() {
        val template = BridgeTestUtil.createMathGameSshMachine(testRootDisposable)
        val localJvmProviderConfig = template.getHostMachineConfig().providers.find { config -> config is LocalJvmProviderConfig }!!
        // Local
        val provider = service<JvmProviderManager>().getProvider(localJvmProviderConfig)
        val jvm = provider.searchJvm(template, localJvmProviderConfig).result!!.find { jvm -> jvm.name.contains("math-game.jar")}!!


        testBridge(jvm, template, localJvmProviderConfig)
    }

    private fun testBridge(
        jvm: JVM,
        template: HostMachine,
        providerConfig: JvmProviderConfig
    ) {
        val executionManager = project.getService(ArthasExecutionManager::class.java) as ArthasExecutionManagerImpl
        val builder = StringBuilder()
        val executedCommand = mutableListOf<String>()
        val executeResult = mutableListOf<String>()
        try {
            val arthasBridge = executionManager.initTemplate(
                jvm,
                template.getHostMachineConfig(),
                providerConfig,
            )
            arthasBridge.addListener(object : ArthasBridgeListener() {
                override fun onContent(result: String) {
                    builder.append(result)
                }

                override fun onFinish(command: String, result: ArthasResultItem, rawContent: String) {
                    executedCommand.add(command)
                    executeResult.add(rawContent)
                }

                override fun onClose() {
                    println("closed")
                }
            })
            arthasBridge.attachNow()
            arthasBridge.waitUntilAttached()

            executionManager.getTemplate(jvm)!!.let {
                it.execute("sc demo.*")
                it.execute("echo hello")
                it.stop()
            }
            Assert.assertEquals(mutableListOf("sc demo.*", "echo hello"), executedCommand)
            Assert.assertTrue(executeResult[0].startsWith("demo.MathGame\nAffect(row-cnt:1)"))
            Assert.assertTrue(executeResult[1].startsWith("hello"))
        } catch (e: Exception) {
            if (System.getenv("RUNNER_DEBUG") == "1") {
                println(builder.toString())
            }
            throw e;
        }
    }

}