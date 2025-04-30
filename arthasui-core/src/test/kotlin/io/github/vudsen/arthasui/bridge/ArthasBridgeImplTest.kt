package io.github.vudsen.arthasui.bridge

import com.intellij.mock.MockApplication
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import io.github.vudsen.arthasui.MockArthasProcess
import io.github.vudsen.arthasui.TestProgressIndicator
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.core.ArthasExecutionManagerImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import org.junit.Assert
import org.junit.BeforeClass
import java.lang.ref.WeakReference

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
     * 所有的换行符都应该被统一为 \n
     */
    fun testLineSeparator() {
        val process = MockArthasProcess()
        val arthasBridge: ArthasBridge = ArthasBridgeImpl(process)
        process.sendReadyMessage()
        val flow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        runBlocking {
            arthasBridge.addListener(object : ArthasBridgeListener() {
                override fun onContent(result: String) {
                    Assert.assertFalse(result.contains('\r'))
                    Assert.assertFalse(result.contains(0.toChar()))
                    flow.tryEmit(Unit)
                }
            })
            launch {
                process.waitClientRequest()
                process.writeResponse("hello\r")
                flow.first()
                process.writeResponse(" world.\r")
                flow.first()
                process.writeResponse("\r\nhow\r\n")
                flow.first()
                process.writeResponse("\r\nare\r")
                flow.first()
                process.writeResponse("\nyou?\r\r\n")
                process.writeResponseEnd()
            }
            launch {
                val item = arthasBridge.execute("any")
                Assert.assertEquals("hello world.\nhow\n\nare\nyou?", item.toString())
            }
        }
    }

    fun testAttachLinuxLocal() {
        val template = BridgeTestUtil.createMathGameSshMachine(testRootDisposable)
        val localJvmProviderConfig = template.getHostMachineConfig().providers.find { config -> config is LocalJvmProviderConfig }!!
        // Local
        val provider = service<JvmProviderManager>().getProvider(localJvmProviderConfig)
        val jvm = provider.searchJvm(template, localJvmProviderConfig).find { jvm -> jvm.name.contains("math-game.jar")}!!

        val executionManager = project.getService(ArthasExecutionManager::class.java) as ArthasExecutionManagerImpl
        // A china's mirror, you can uncomment it and use it on local but please do not commit.
        executionManager.githubApiMirror = "https://5j9g3t.site/github-mirror"
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
            runBlocking {
                arthasBridge.attachNow()
                arthasBridge.waitUntilAttached()

                executionManager.getTemplate(jvm)!!.let {
                    it.execute("sc demo.*")
                    it.stop()
                }
            }
            Assert.assertEquals(mutableListOf("sc demo.*", "stop"), executedCommand)
            Assert.assertTrue(executeResult[0].startsWith("demo.MathGame\nAffect(row-cnt:1)"))
            Assert.assertTrue(executeResult[1].startsWith("Resetting all enhanced classes ..."))
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