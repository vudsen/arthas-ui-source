package io.github.vudsen.arthasui.bridge.providers.tunnel

import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

class TunnelServerArthasBridgeFactory(
    private val config: TunnelServerConnectConfig,
    private val jvm: JVM
) : ArthasBridgeFactory {

    companion object {
        private class FakeInputStream(private val websocket: WebSocket) : OutputStream() {

            override fun write(b: Int) {
                websocket.send(ByteString.of(b.toByte()))
            }

            override fun write(b: ByteArray) {
                websocket.send(ByteString.of(*b))
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                websocket.send(b.toByteString(off, len))
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
            websocket.close(0, "Client exit.")
        }

    }

    private inner class MyWebSocketListener(private val outputStream: OutputStream) : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            outputStream.write(bytes.toByteArray())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            exitCode = code
        }
    }



    override fun createBridge(): ArthasBridge {
        // ws://localhost:7777/ws?method=connectArthas&id=demoapp_HXJHL8BDDCJHW75JIXJQ
        val client = OkHttpClient()
        val request = Request.Builder().url(config.wsPath + "?method=connectArthas&id=${jvm.id}").build()

        val actualInput = PipedInputStream()

        val ws = client.newWebSocket(request, MyWebSocketListener(PipedOutputStream(actualInput)))
        return ArthasBridgeImpl(WebSocketInteractiveShell(actualInput, FakeInputStream(ws), ws))
    }


}