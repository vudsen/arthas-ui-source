package io.github.vudsen.arthasui.api.conf

import com.intellij.openapi.components.service
import com.intellij.util.xmlb.Converter


interface HostMachineConnectConfigConverter {

    fun toString(p0: HostMachineConnectConfig): String?

    fun fromString(p0: String): HostMachineConnectConfig?

    companion object {
        class MyConverter : Converter<HostMachineConnectConfig>() {

            private val delegate: HostMachineConnectConfigConverter by lazy {
                service<HostMachineConnectConfigConverter>()
            }

            override fun toString(p0: HostMachineConnectConfig): String? {
                return delegate.toString(p0)
            }

            override fun fromString(p0: String): HostMachineConnectConfig? {
                return delegate.fromString(p0)
            }
        }
    }

}