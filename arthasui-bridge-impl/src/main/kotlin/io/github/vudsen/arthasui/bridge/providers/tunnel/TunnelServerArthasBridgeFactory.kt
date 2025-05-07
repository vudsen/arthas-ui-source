package io.github.vudsen.arthasui.bridge.providers.tunnel

import com.intellij.util.io.toByteArray
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class TunnelServerArthasBridgeFactory(
    private val config: TunnelServerConnectConfig,
    private val jvm: JVM
) : ArthasBridgeFactory {

    companion object {
        private class FakeInputStream(private val websocket: WebSocket) : OutputStream() {

            override fun write(b: Int) {
                websocket.sendBinary(ByteBuffer.allocate(1).apply {
                    put(b.toByte())
                }, true).get()
            }

            override fun write(b: ByteArray) {
                websocket.sendBinary(ByteBuffer.wrap(b), true).get()
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                websocket.sendBinary(ByteBuffer.wrap(b, off, len), true).get()
            }

        }
    }

    private var exitCode: Int? = null

    private inner class WebSocketInteractiveShell(
        private val inputStream: InputStream,
        private val outputStream: OutputStream,
        private val websocket: WebSocket
    ) : InteractiveShell {
        override fun getInputStream(): InputStream {
            return inputStream
        }

        override fun getOutputStream(): OutputStream {
            return outputStream
        }

        override fun isAlive(): Boolean {
            return exitCode == null
        }

        override fun exitCode(): Int? {
            return exitCode
        }

        override fun close() {
            websocket.sendClose(0, "Client exit.")
        }

    }

    private inner class MyWebSocketListener(private val outputStream: OutputStream, private val future: CompletableFuture<Unit>) : WebSocket.Listener {

        override fun onBinary(
            webSocket: WebSocket?,
            data: ByteBuffer,
            last: Boolean
        ): CompletionStage<*>? {
            outputStream.write(data.toByteArray())
            return super.onBinary(webSocket, data, last)
        }

        override fun onText(webSocket: WebSocket?, data: CharSequence, last: Boolean): CompletionStage<*>? {
            outputStream.write(data.toString().toByteArray())
            return super.onText(webSocket, data, last)
        }

        override fun onOpen(webSocket: WebSocket?) {
            future.complete(Unit)
            super.onOpen(webSocket)
        }

        override fun onClose(
            webSocket: WebSocket?,
            statusCode: Int,
            reason: String?
        ): CompletionStage<*>? {
            exitCode = statusCode
            if (future.isDone) {
                future.completeExceptionally(IllegalStateException(reason))
            }
            return super.onClose(webSocket, statusCode, reason)
        }

        override fun onError(webSocket: WebSocket?, error: Throwable?) {
            super.onError(webSocket, error)
        }
    }


    override fun createBridge(): ArthasBridge {
        // ws://localhost:7777/ws?method=connectArthas&id=demoapp_HXJHL8BDDCJHW75JIXJQ
        val client = HttpClient.newHttpClient()
        val actualInput = PipedInputStream()

        val future = CompletableFuture<Unit>()

        val webSocket = client.newWebSocketBuilder()
            .buildAsync(
                URI.create(config.wsPath + "?method=connectArthas&id=${jvm.id}&targetServer=192.168.1.199"),
                MyWebSocketListener(PipedOutputStream(actualInput), future)
            )
            .join()

        future.get()
        return ArthasBridgeImpl(WebSocketInteractiveShell(actualInput, FakeInputStream(webSocket), webSocket))
    }


}