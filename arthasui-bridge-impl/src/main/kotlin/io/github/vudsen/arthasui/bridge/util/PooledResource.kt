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
    private val factory: Factory<T>,
) : InvocationHandler {

    private var delegate: WeakReference<ManagedInstance<T>> = WeakReference(null)

    private val connectionManager = service<HostMachineConnectionManager>()


    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val actualArgs: Array<out Any?> = args ?: emptyArray()
        delegate.get() ?.let {
            if (it.resource.isClosed()) {
                connectionManager.reportClosed(it)
            } else if (method.name == "close"){
                connectionManager.reportClosed(it)
                return method.invoke(it.resource, *actualArgs)
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