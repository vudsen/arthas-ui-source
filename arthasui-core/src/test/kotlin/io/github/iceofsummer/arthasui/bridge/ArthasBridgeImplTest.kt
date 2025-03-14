package io.github.vudsen.arthasui.bridge

import com.intellij.mock.MockApplication
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.MockArthasProcess
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class ArthasBridgeImplTest {


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
    @Test
    fun execute_testLineSeparator() {
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

}