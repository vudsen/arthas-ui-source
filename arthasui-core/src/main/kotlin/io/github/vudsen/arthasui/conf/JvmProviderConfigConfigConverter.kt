package io.github.vudsen.arthasui.conf

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.intellij.util.xmlb.Converter
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig

class JvmProviderConfigConfigConverter : Converter<MutableList<JvmProviderConfig>>() {

    private val gson: Gson
    init {
        val adapterFactory = RuntimeTypeAdapterFactory.of(JvmProviderConfig::class.java, "type")
            .registerSubtype(LocalJvmProviderConfig::class.java, LocalJvmProviderConfig.TYPE)
            .registerSubtype(JvmInDockerProviderConfig::class.java, JvmInDockerProviderConfig.TYPE)
        gson = GsonBuilder().registerTypeAdapterFactory(adapterFactory).create()
    }


    override fun toString(value: MutableList<JvmProviderConfig>): String {
        return gson.toJson(value)
    }

    override fun fromString(value: String): MutableList<JvmProviderConfig> {
        return gson.fromJson(value, object : TypeToken<MutableList<JvmProviderConfig>>() {})
    }

}