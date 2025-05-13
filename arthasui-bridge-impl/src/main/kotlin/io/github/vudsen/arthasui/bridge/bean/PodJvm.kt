package io.github.vudsen.arthasui.bridge.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

/**
 * K8s Pod
 */
class PodJvm(
    id: String,
    name: String,
    context: JvmContext,
    /**
     * 命名空间
     */
    var namespace: String,
    /**
     * 容器名称
     */
    var containerName: String?,
) : JVM(id, name, context) {

    override fun getIcon(): Icon {
        return ArthasUIIcons.Local
    }


}