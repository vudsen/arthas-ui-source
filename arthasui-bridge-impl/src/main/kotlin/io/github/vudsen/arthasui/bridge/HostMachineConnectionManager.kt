package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import io.github.vudsen.arthasui.api.AutoCloseableWithState
import io.github.vudsen.arthasui.common.util.LRUCache
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * 宿主机连接管理，若宿主机在 [timeoutMilliseconds] 毫秒内没有使用，则会自动关闭。
 */
@Service(Service.Level.APP)
class HostMachineConnectionManager {

    var timeoutMilliseconds = 1000 * 60 * 3L

    companion object {
        val logger = Logger.getInstance(HostMachineConnectionManager::class.java)

        class ManagedInstance<T : AutoCloseableWithState>(
            var resource: T,
            var lastUse: Long = System.currentTimeMillis(),
        )

    }

    private val lru = LRUCache<ManagedInstance<out AutoCloseableWithState>>()

    private val lock = ReentrantLock()

    private val executor = AppExecutorUtil.getAppScheduledExecutorService()

    private var future: ScheduledFuture<*>? = null


    /**
     * 尝试计划下个任务
     * @param onlyOneTask 确保只有一个任务。通常用于在一个正在运行的任务中创建另外一个任务
     */
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
                node.lastUse + timeoutMilliseconds - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
            )
        } finally {
            lock.unlock()
        }
    }

    inner class ConnectionCloseRunnable : Runnable {

        override fun run() {
            var node = lru.peek()
            if (System.currentTimeMillis() - node.lastUse < timeoutMilliseconds) {
                tryScheduleNext(false)
                return
            }
            lock.lock()
            try {
                node = lru.peek()
                if (System.currentTimeMillis() - node.lastUse < timeoutMilliseconds) {
                    tryScheduleNext(false)
                    return
                }
                lru.poll()
            } finally {
                lock.unlock()
            }
            try {
                if (node.resource.isCloseable()) {
                    logger.info("Try to close ${node.resource}")
                    node.resource.close()
                } else {
                    reEnqueue(node)
                }
            } catch (e: Exception) {
                logger.error("Failed to close host machine", e)
            } finally {
                tryScheduleNext(false)
            }
        }

    }

    private fun <T : AutoCloseableWithState> reEnqueue(resource: ManagedInstance<T>) {
        lock.lock()
        try {
            resource.lastUse = System.currentTimeMillis()
            lru.add(resource)
        } finally {
            lock.unlock()
        }
    }

    /**
     * 注册一个宿主机，被注册的宿主机如果在一定时间内没有被使用，将会自动关闭连接
     * @param resource 宿主机，如果重复注册，则不会发生任何事
     */
    fun <T : AutoCloseableWithState> register(resource: T): ManagedInstance<T> {
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
    fun <T : AutoCloseableWithState> resetTimeout(node: ManagedInstance<T>) {
        logger.info("Timeout rested: ${node.resource}, last use: ${node.lastUse}")
        node.lastUse = System.currentTimeMillis()
        lock.lock()
        try {
            if (lru.refresh(node)) {
                return
            }
            lru.add(node)
            tryScheduleNext(true)
        } finally {
            lock.unlock()
        }
    }

    /**
     * 报告实例已经关闭
     *
     * 注意: **调用该方法不会关闭实例，仍然需要自己主动关闭!**
     */
    fun <T : AutoCloseableWithState> reportClosed(node: ManagedInstance<T>) {
        lock.lock()
        try {
            lru.remove(node)
            tryScheduleNext(true)
        } finally {
            lock.unlock()
        }
    }

}