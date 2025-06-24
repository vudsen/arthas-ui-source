package io.github.vudsen.arthasui.bridge.util

import com.intellij.openapi.components.service
import com.intellij.openapi.util.Factory
import io.github.vudsen.arthasui.api.AutoCloseableWithState
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager.Companion.ManagedInstance
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * 缓存实例，在必要的时候重新创建
 *
 * 调用方法类型分为:
 * 1. 调用状态检查方法，例如 `isClosed`、`isCloseable`
 *
 *    a. 如果没有缓存的实例：返回 true
 *
 *    b. 如果有缓存的实例：调用对应的方法并返回，不刷新上次使用时间
 * 2. 调用关闭方法，例如 `close`
 *
 *    a. 如果没有缓存的实例：无事发生，返回空
 *
 *    b. 如果有缓存的实例：关闭对应的实例
 * 3. 调用其他方法
 *
 *    a. 如果没有缓存的实例：创建新的实例并注册
 *
 *    b. 如果有缓存的实例：调用对应方法并且刷新上传使用时间
 *
 * ---
 * 存疑：`isClosed` 是否还有必要?
 */
class PooledResource<T : AutoCloseableWithState>(
    private val factory: Factory<T>,
) : InvocationHandler {

    private var delegate: WeakReference<ManagedInstance<T>> = WeakReference(null)

    private var firstInvokeFlag = false

    private val connectionManager = service<HostMachineConnectionManager>()

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val actualArgs: Array<out Any?> = args ?: emptyArray()

        var instance = delegate.get()
        if (instance != null && instance.resource.isClosed()) {
            instance = null
        }

        when (method.name) {
            "isClosed", "isCloseable" -> {
                if (instance == null) {
                    // 对于第一次创建时，`isClosed` 应该为 false 以防止刚创建就被认为关闭了。
                    return firstInvokeFlag
                } else {
                    return method.invoke(instance.resource, *actualArgs)
                }
            }
            "close" -> {
                if (instance == null) {
                    firstInvokeFlag = true
                    return null
                } else {
                    connectionManager.reportClosed(instance)
                    return method.invoke(instance.resource, *actualArgs)
                }
            }
            else -> {
                if (instance == null || instance.resource.isClosed()) {
                    val managedInstance = connectionManager.register(factory.create())
                    this.delegate = WeakReference(managedInstance)
                    firstInvokeFlag = true
                    return method.invoke(managedInstance.resource, *actualArgs)
                } else {
                    connectionManager.resetTimeout(instance)
                    return method.invoke(instance.resource, *actualArgs)
                }
            }
        }
    }

}