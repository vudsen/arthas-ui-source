package io.github.vudsen.arthasui.bridge.util

import com.intellij.openapi.components.service
import com.intellij.openapi.util.Factory
import io.github.vudsen.arthasui.api.AutoCloseableWithState
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager.Companion.ManagedInstance
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class PooledResource<T : AutoCloseableWithState>(
    initialInstance: T?,
    /**
     * 若资源还未被创建，并且调用的方法被忽略时，将会调用该对象的方法
     */
    private val fallback: T,
    private val factory: Factory<T>,
) : InvocationHandler {

    private var delegate: WeakReference<ManagedInstance<T>> = WeakReference(null)

    private val connectionManager = service<HostMachineConnectionManager>()

    init {
        initialInstance ?.let {
            val managedInstance = connectionManager.register(it)
            this.delegate = WeakReference(managedInstance)
        }
    }

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val actualArgs: Array<out Any?> = args ?: emptyArray()
        if (!method.isAnnotationPresent(RefreshState::class.java)) {
            delegate.get() ?.let {
                if (it.resource.isClosed()) {
                    connectionManager.reportClosed(it)
                } else {
                    return method.invoke(it.resource, *actualArgs)
                }
            } ?: return method.invoke(fallback, *actualArgs)
        }
        delegate.get() ?.let {
            if (it.resource.isClosed()) {
                connectionManager.reportClosed(it)
            } else {
                connectionManager.resetTimeout(it)
                return method.invoke(it.resource, *actualArgs)
            }
        }
        val managedInstance = connectionManager.register(factory.create())
        this.delegate = WeakReference(managedInstance)
        return method.invoke(managedInstance.resource, *actualArgs)
    }

}