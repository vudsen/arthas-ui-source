package io.github.vudsen.arthasui.common.util

import com.google.gson.Gson
import com.intellij.openapi.components.Service

/**
 * 保存各种单例
 */
@Service(Service.Level.APP)
class SingletonInstanceHolderService {

    val gson = Gson()

}