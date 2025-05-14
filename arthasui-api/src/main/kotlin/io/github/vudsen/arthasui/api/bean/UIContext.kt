package io.github.vudsen.arthasui.api.bean

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project

class UIContext(
    val parentDisposable: Disposable,
    val project: Project? = null
)