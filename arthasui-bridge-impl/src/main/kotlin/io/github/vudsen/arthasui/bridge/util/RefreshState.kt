package io.github.vudsen.arthasui.bridge.util

/**
 * 配合 [PooledResource] 使用, 当标注的方法被调用后，将会刷新上次使用时间以避免被关闭。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RefreshState()
