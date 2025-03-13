package io.github.vudsen.arthasui.conf

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EditorTextField
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.conf.bean.JvmSearchGroup
import io.github.vudsen.arthasui.language.ognl.psi.OgnlFileType
import io.github.vudsen.arthasui.script.OgnlJvmSearcher
import io.github.vudsen.arthasui.script.SearcherRootState
import io.github.vudsen.arthasui.util.ui.KMutableProperty2MutablePropertyAdapter;
import javax.swing.JComponent

class JvmSearchGroupConfigurable(
    private val project: Project
//    private val hostMachine: HostMachine ,
//    private val hostMachineConfigV2: HostMachineConfigV2 = HostMachineConfigV2(),
) : Configurable {

    private val state = State()

    private var root: DialogPanel? = null

    companion object {
        data class State(
            var name: String = "",
            var script: String = ""
        )
    }

    override fun createComponent(): JComponent? {
        val ognlTextArea = EditorTextField(project, OgnlFileType)

        val root = panel {
            row {
                textField().label("Name").bindText(state::name)
            }
            group("Ognl Script") {
                row {
                    cell(ognlTextArea).bind({ area: EditorTextField ->
                        return@bind area.text
                    }, { area: EditorTextField, value: String ->
                        area.text = value
                    }, KMutableProperty2MutablePropertyAdapter(state::script))
                }
            }
            row {
                button("Test Script") {
                    testScript(ognlTextArea.text)
                }
            }
        }

        this.root = root
        return root
    }

    private fun testScript(script: String) {
        try {
            OgnlJvmSearcher.search(script, SearcherRootState())
        } catch (e: Exception) {
            TODO("Tip user script execute failed.")
        }
    }

    override fun isModified(): Boolean {
        return root?.isModified() ?: false
    }

    override fun apply() {
        val root = root ?: return
        root.apply()
        if (root.validateAll().isNotEmpty()) {
            return
        }
//        val entity = JvmSearchGroup(state.name, hostMachineConfigV2.name, state.script)
//        val persistent = project.getService(ArthasUISettingsPersistent::class.java)
//
//        val target = persistent.state.hostMachines.find { config -> config == hostMachineConfigV2 }
//        target ?: let {
//            throw IllegalStateException("Unreachable code.")
//        }
//
//        val newSearchGroup = ArrayList<JvmSearchGroup>(target.searchGroups.size)
//        for (searchGroup in target.searchGroups) {
//            if (searchGroup == entity) {
//                newSearchGroup.add(entity)
//            } else {
//                newSearchGroup.add(searchGroup)
//            }
//        }
//        if (newSearchGroup.size != target.searchGroups.size) {
//            newSearchGroup.add(entity)
//        }
//
//        target.searchGroups = newSearchGroup
    }

    override fun getDisplayName(): String {
        return "Jvm Search Group"
    }


}