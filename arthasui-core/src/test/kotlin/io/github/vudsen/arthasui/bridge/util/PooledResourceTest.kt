package io.github.vudsen.arthasui.bridge.util

import com.intellij.openapi.components.service
import com.intellij.openapi.util.Key
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.HostMachineConnectionManager
import org.junit.Assert
import java.lang.reflect.Proxy

class PooledResourceTest : BasePlatformTestCase() {

    private interface MySubCloseableHostMachine : CloseableHostMachine {

        fun setCloseable(b: Boolean)

    }

    private class TestMachine : MySubCloseableHostMachine {

        private var closed = false

        private var closeable = true

        override fun isClosed(): Boolean {
            return closed
        }

        override fun isCloseable(): Boolean {
            return closeable
        }

        override fun close() {
            closed = true
        }

        override fun getOS(): OS {
            return OS.LINUX
        }

        override fun getConfiguration(): HostMachineConnectConfig {
            throw IllegalStateException("Not implemented")
        }

        override fun getHostMachineConfig(): HostMachineConfig {
            throw IllegalStateException("Not implemented")
        }

        override fun test() {}

        override fun <T : Any?> getUserData(p0: Key<T?>): T? {
            throw IllegalStateException("Not implemented")
        }

        override fun <T : Any?> putUserData(p0: Key<T?>, p1: T?) {
            throw IllegalStateException("Not implemented")
        }

        override fun setCloseable(b: Boolean) {
            closeable = b
        }

    }

    private fun createPooledResource(): MySubCloseableHostMachine {
        return Proxy.newProxyInstance(
            TestMachine::class.java.classLoader,
            arrayOf(MySubCloseableHostMachine::class.java),
            PooledResource { TestMachine() }
        ) as MySubCloseableHostMachine
    }


    fun testAutoclose() {
        val manager = service<HostMachineConnectionManager>()
        val old = manager.timeoutMilliseconds
        try {
            manager.timeoutMilliseconds = 1000 * 2
            val machine = createPooledResource()
            // 测试刚创建时，是否关闭为 true
            Assert.assertFalse(machine.isClosed())
            machine.test()
            Thread.sleep(2500)
            // 测试是否被自动关闭了
            Assert.assertTrue(machine.isClosed())
            // 设置不可关闭
            machine.setCloseable(false)
            machine.test()
            Thread.sleep(2500)
            // 不应该被关闭
            Assert.assertFalse(machine.isClosed())
            // 恢复可关闭
            machine.setCloseable(true)
            Thread.sleep(2500)
            // 应该被关闭
            Assert.assertTrue(machine.isClosed())
        } finally {
            manager.timeoutMilliseconds = old
        }

    }
}