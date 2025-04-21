package io.github.vudsen.arthasui.bridge.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfigConfigConverter
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig

class JvmProviderConfigConfigConverterImpl : JvmProviderConfigConfigConverter {

    private val gson: Gson
    init {
        val adapterFactory = RuntimeTypeAdapterFactory.of(JvmProviderConfig::class.java, "type")
            .registerSubtype(LocalJvmProviderConfig::class.java, LocalJvmProviderConfig.TYPE)
            .registerSubtype(JvmInDockerProviderConfig::class.java, JvmInDockerProviderConfig.TYPE)
        gson = GsonBuilder().registerTypeAdapterFactory(adapterFactory).create()
    }


    override fun toString(p0: MutableList<JvmProviderConfig>): String {
        return gson.toJson(p0)
    }

    override fun fromString(p0: String): MutableList<JvmProviderConfig> {
        return gson.fromJson(p0, object : TypeToken<MutableList<JvmProviderConfig>>() {})
    }

}