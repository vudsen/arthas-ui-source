package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import io.github.vudsen.arthasui.common.util.LRUCache
import java.io.Closeable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * 宿主机连接管理，若宿主机在 [TIMEOUT_MILLISECONDS] 毫秒内没有使用，则会自动关闭。
 */
@Service(Service.Level.APP)
class HostMachineConnectionManager {

    companion object {
        val logger = Logger.getInstance(HostMachineConnectionManager::class.java)

        const val TIMEOUT_MILLISECONDS = 1000 * 60 * 3L

        class ManagedInstance<T : AutoCloseable>(
            var resource: T,
            var lastUse: Long = System.currentTimeMillis(),
        )

    }

    private val lru = LRUCache<ManagedInstance<out AutoCloseable>>()

    private val lock = ReentrantLock()

    private val executor = AppExecutorUtil.getAppScheduledExecutorService()

    private var future: ScheduledFuture<*>? = null


    private fun tryScheduleNext(onlyOneTask: Boolean) {
        future ?.let {
            if (!it.isDone && onlyOneTask) {
                return
            }
        }
        lock.lock()
        try {
            if (lru.isEmpty()) {
                return
            }
            future ?.let {
                if (!it.isDone && onlyOneTask) {
                    return
                }
            }
            val node = lru.peek()
            this.future = executor.schedule(
                ConnectionCloseRunnable(),
                node.lastUse + TIMEOUT_MILLISECONDS - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
            )
        } finally {
            lock.unlock()
        }
    }

    inner class ConnectionCloseRunnable : Runnable {

        override fun run() {
            var node = lru.peek()
            if (System.currentTimeMillis() - node.lastUse < TIMEOUT_MILLISECONDS) {
                tryScheduleNext(false)
                return
            }
            lock.lock()
            try {
                node = lru.peek()
                if (System.currentTimeMillis() - node.lastUse < TIMEOUT_MILLISECONDS) {
                    tryScheduleNext(false)
                    return
                }
                lru.poll()
            } finally {
                lock.unlock()
            }
            logger.info("Try to close ${node.resource}")
            try {
                node.resource.close()
            } catch (e: Exception) {
                logger.error("Faild to close host machine", e)
            } finally {
                tryScheduleNext(false)
            }
        }

    }


    /**
     * 注册一个宿主机，被注册的宿主机如果在一定时间内没有被使用，将会自动关闭连接
     * @param resource 宿主机，如果重复注册，则不会发生任何事
     */
    fun <T : AutoCloseable> register(resource: T): ManagedInstance<T> {
        logger.info("Registered $resource")
        val node = ManagedInstance(resource)
        lock.lock()
        try {
            lru.add(node)
            tryScheduleNext(true)
        } finally {
            lock.unlock()
        }
        return node
    }


    /**
     * 重置宿主机的自动关闭时间.
     */
    fun <T : AutoCloseable> resetTimeout(node: ManagedInstance<T>) {
        logger.info("Timeout rested: ${node.resource}, last use: ${node.lastUse}")
        node.lastUse = System.currentTimeMillis()
        lock.lock()
        try {
            if (lru.refresh(node)) {
                return
            }
            lru.add(node)
            tryScheduleNext(false)
        } finally {
            lock.unlock()
        }
    }

}