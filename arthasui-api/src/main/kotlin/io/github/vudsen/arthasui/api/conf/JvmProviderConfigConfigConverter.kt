package io.github.vudsen.arthasui.api.conf

import com.intellij.openapi.components.service
import com.intellij.util.xmlb.Converter

interface JvmProviderConfigConfigConverter {


    fun toString(p0: MutableList<JvmProviderConfig>): String?

    fun fromString(p0: String): MutableList<JvmProviderConfig>?

    companion object {
        class MyConverter : Converter<MutableList<JvmProviderConfig>>() {

            private val delegate: JvmProviderConfigConfigConverter by lazy {
                service<JvmProviderConfigConfigConverter>()
            }

            override fun toString(p0: MutableList<JvmProviderConfig>): String? {
                return delegate.toString(p0)
            }

            override fun fromString(p0: String): MutableList<JvmProviderConfig>? {
                return delegate.fromString(p0)
            }

        }
    }

}