package io.github.vudsen.arthasui.util.ui

import com.intellij.ui.dsl.builder.MutableProperty
import kotlin.reflect.KMutableProperty

class KMutableProperty2MutablePropertyAdapter<T>(private val property: KMutableProperty<T>) : MutableProperty<T> {
    override fun get(): T {
        return property.getter.call()
    }

    override fun set(value: T) {
        property.setter.call(value)
    }

}