package io.github.vudsen.arthasui.api

interface DeepCopyable<T> {

    /**
     * 深拷贝
     */
    fun deepCopy(): T

}