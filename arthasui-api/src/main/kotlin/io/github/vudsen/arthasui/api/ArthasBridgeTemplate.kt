package io.github.vudsen.arthasui.api

import com.intellij.openapi.application.ApplicationManager
import kotlinx.coroutines.CancellationException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 增强 [ArthasBridge]，提供懒加载。
 *
 * 实际的 [ArthasBridge] 将会在第一次执行命令时初始化，除此之外可以直接调用 [attachNow] 立即连接。 
 */
class ArthasBridgeTemplate(private val factory: ArthasBridgeFactory) :
    ArthasBridge {

    private var delegate: ArthasBridge? = null

    private val proxy = Proxy.newProxyInstance(this.javaClass.classLoader, arrayOf(ArthasBridge::class.java), ProxiedArthasBridge()) as ArthasBridge

    private val stashedListeners = CopyOnWriteArrayList<ArthasBridgeListener>()

    private val attachFuture = CompletableFuture<Unit>()

    private inner class ProxiedArthasBridge : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any {
            val bridge = getOrInitOriginalBridge()
            return method.invoke(bridge, *args)
        }
    }

    private fun getOrInitOriginalBridge(): ArthasBridge {
        delegate ?.let { return it }
        synchronized(ArthasBridge::class.java) {
            delegate ?.let { return it }
            try {
                val bridge = factory.createBridge()
                afterBridgeCreated(bridge)
                delegate = bridge
                attachFuture.complete(Unit)
                return bridge
            } catch (e: Exception) {
                notifyBridgeCreateError(e)
                throw e
            }
        }
    }

    override fun execute(command: String): ArthasResultItem {
        return proxy.execute(command)
    }

    override fun isAlive(): Boolean {
        return delegate?.isAlive() ?: false
    }

    override fun isClosed(): Boolean {
        return delegate?.isClosed() ?: false
    }

    /**
     * 不执行任何命令，立即 attach
     */
    fun attachNow() {
        getOrInitOriginalBridge()
    }

    private fun notifyClose() {
        ApplicationManager.getApplication().invokeLater {
            for (listener in stashedListeners) {
                listener.onClose()
            }
        }
    }

    /**
     * 通知创建 Bridge 失败，并唤醒所有由于 [waitUntilAttached] 而挂起的协程
     */
    private fun notifyBridgeCreateError(e: Exception) {
        if (!attachFuture.isDone) {
            attachFuture.completeExceptionally(e)
        }
        notifyClose()
    }


    override fun addListener(arthasBridgeListener: ArthasBridgeListener) {
        val d = delegate
        if (d == null) {
            stashedListeners.add(arthasBridgeListener)
        } else {
            d.addListener(arthasBridgeListener)
        }
    }

    override fun stop(): Int {
        if (!attachFuture.isDone) {
            attachFuture.completeExceptionally(CancellationException())
        }
        delegate ?.let {
            return it.stop()
        } ?: let {
            notifyClose()
            return 0
        }
    }

    override fun isBusy(): Boolean {
        return delegate?.isBusy() == true
    }

    /**
     * 此时锁还没有释放
     */
    private fun afterBridgeCreated(arthasBridge: ArthasBridge) {
        for (listener in stashedListeners) {
            arthasBridge.addListener(listener)
        }
        stashedListeners.clear()
    }

    /**
     * 挂起当前协程，直到 attach 成功
     */
    fun waitUntilAttached() {
        attachFuture.get()
    }

}
