package io.github.vudsen.arthasui.bridge

import com.intellij.mock.MockApplication
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import io.github.vudsen.arthasui.TestProgressIndicator
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.core.ArthasExecutionManagerImpl
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.BeforeClass

class ArthasBridgeImplTest : BasePlatformTestCase() {


    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            val disposable = Disposable { }
            ApplicationManager.setApplication(MockApplication(disposable), disposable)

            val logger = Logger.getInstance(ArthasBridgeImpl::class.java)
            logger.setLevel(LogLevel.DEBUG)
        }
    }


    /**
     * Test attach to linux machine.
     *
     * For chinese developer, you can add environment variable `TOOLCHAIN_MIRROR=https://5j9g3t.site/github-mirror`
     * to avoid download failed from GitHub.
     */
    fun testAttachLinuxLocal() {
        val template = BridgeTestUtil.createMathGameSshMachine(testRootDisposable)
        val localJvmProviderConfig = template.getHostMachineConfig().providers.find { config -> config is LocalJvmProviderConfig }!!
        // Local
        val provider = service<JvmProviderManager>().getProvider(localJvmProviderConfig)
        val jvm = provider.searchJvm(template, localJvmProviderConfig).result!!.find { jvm -> jvm.name.contains("math-game.jar")}!!


        val executionManager = project.getService(ArthasExecutionManager::class.java) as ArthasExecutionManagerImpl
        val builder = StringBuilder()
        val executedCommand = mutableListOf<String>()
        val executeResult = mutableListOf<String>()
        try {
            val arthasBridge = executionManager.initTemplate(
                jvm,
                template.getHostMachineConfig(),
                localJvmProviderConfig,
                TestProgressIndicator()
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
        } finally {
            executionManager.githubApiMirror = null
        }
    }

}