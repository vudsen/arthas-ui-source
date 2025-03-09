package io.github.vudsen.arthasui.api

import io.github.vudsen.arthasui.api.exception.BridgeIsBusyException
import io.github.vudsen.arthasui.api.exception.BridgeWaitInitTimeoutException
import kotlinx.coroutines.CompletableDeferred
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 增强 [ArthasBridge]，提供原子化、懒加载等操作。
 *
 * 实际的 [ArthasBridge] 将会在第一次执行命令时初始化，除此之外可以直接调用 [attachNow] 立即连接。
 *
 * 当出现冲突时，即一个命令还没有执行完，又需要执行下一个命令，将会抛出一个
 * [io.github.vudsen.arthasui.bridge.exception.BridgeIsBusyException]。
 *
 * 可以通过 [isBusy] 来判断当前是否有命令正在执行，但该状态不一定准确，如果需要执行命令，则不应该依赖于此状态。
 */
class ArthasBridgeTemplate(private val factory: ArthasBridgeFactory) :
    ArthasBridge {

    private var delegate: ArthasBridge? = null

    private val spinLock: AtomicBoolean = AtomicBoolean(false)

    private val proxy = Proxy.newProxyInstance(this.javaClass.classLoader, arrayOf(ArthasBridge::class.java), ProxiedArthasBridge()) as ArthasBridge

    private val stashedListeners = CopyOnWriteArrayList<ArthasBridgeListener>()

    private val attachDeferred = CompletableDeferred<Unit>()

    private inner class ProxiedArthasBridge : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any {
            val bridge = getOrInitOriginalBridge()
            if (!spinLock.compareAndSet(false, true)) {
                throw BridgeIsBusyException("Bridge is busy!")
            }
            try {
                return method.invoke(bridge, *args)
            } finally {
                spinLock.set(false)
            }
        }
    }

    private fun getOrInitOriginalBridge(): ArthasBridge {
        delegate ?.let { return it }
        var spin = 10
        var isNew = false
        while (spin > 0) {
            spin--
            delegate ?.let { return it }
            try {
                if (spinLock.compareAndSet(false, true)) {
                    val bridge = factory.createBridge()
                    afterBridgeCreated(bridge)
                    delegate = bridge
                    isNew = true
                    return bridge
                }
            } finally {
                spinLock.set(false)
                if (isNew) {
                    attachDeferred.complete(Unit)
                }
            }
            Thread.sleep(1000)
        }
        throw BridgeWaitInitTimeoutException("Wait timeout.")
    }

    override suspend fun execute(command: String): ArthasResultItem {
        return proxy.execute(command)
    }

    override fun isAlive(): Boolean {
        return delegate?.isAlive() ?: true
    }

    /**
     * 不执行任何命令，立即 attach
     */
    fun attachNow() {
        getOrInitOriginalBridge()
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
        return delegate?.stop() ?: 0
    }

    override suspend fun cancel() {
        delegate?.cancel()
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
    suspend fun waitUntilAttached() {
        attachDeferred.await()
    }

    /**
     * 查询是否有命令还在执行
     * @return 返回 true 表示有命令正在执行，但并不担保后续时间一定会有命令在执行。
     */
    fun isBusy(): Boolean {
        return spinLock.get()
    }
}