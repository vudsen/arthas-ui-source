package io.github.vudsen.arthasui.conf

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.intellij.util.xmlb.Converter
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.api.bean.EmptyConnectConfig


class HostMachineConnectConfigConverter : Converter<HostMachineConnectConfig>() {

    private val gson: Gson
        init {
            val adapterFactory = RuntimeTypeAdapterFactory.of(HostMachineConnectConfig::class.java, "type")
                .registerSubtype(EmptyConnectConfig::class.java, EmptyConnectConfig.TYPE)
                .registerSubtype(LocalConnectConfig::class.java, LocalConnectConfig.TYPE)
                .registerSubtype(SshHostMachineConnectConfig::class.java, SshHostMachineConnectConfig.TYPE)
            gson = GsonBuilder().registerTypeAdapterFactory(adapterFactory).create()
        }


    override fun toString(value: HostMachineConnectConfig): String? {
        return gson.toJson(value)
    }

    override fun fromString(value: String): HostMachineConnectConfig? {
        return gson.fromJson(value, HostMachineConnectConfig::class.java)
    }

}