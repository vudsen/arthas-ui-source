package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import javax.swing.JComponent
import javax.swing.JList

class RootConfigUI(project: Project) : Disposable {

    /**
     * 临时状态.
     */
    val settingState: SettingsUIState

    private var root: DialogPanel? = null

    private var modified = false

    init {
        val service = project.getService(ArthasUISettingsPersistent::class.java)
        settingState = SettingsUIState(service.state.hostMachines)
    }

    fun isModified(): Boolean {
        val panel = root ?: return false
        panel.apply()
        val pm = panel.isModified()
        if ((pm && panel.validateAll().isEmpty()) || modified) {
            return true
        }
        return false
    }

    fun resetModifiedStatus() {
        modified = false
    }


    fun component(): DialogPanel {
        var currentRoot = root
        if (currentRoot != null) {
            return currentRoot
        }
        currentRoot = panel {
            group("Host Machines:") {
                row {
                    cell(createMainTable()).align(Align.FILL)
                }
            }
        }

        currentRoot.registerValidators {}
        this.root = currentRoot
        return currentRoot
    }

    private fun createMainTable(): JComponent {
        val collectionListModel = CollectionListModel(settingState.hostMachines, true)
        val table = JBList(collectionListModel)

        table.setCellRenderer(object : ColoredListCellRenderer<HostMachineConfigV2>() {

            override fun customizeCellRenderer(
                list: JList<out HostMachineConfigV2>,
                value: HostMachineConfigV2?,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                value ?: return
                this.icon = value.connect.getIcon()
                this.append(value.name)
            }
        })

        val toolbarDecorator = ToolbarDecorator.createDecorator(table)
            .setRemoveAction {
                val jbTable = it.contextComponent as JBList<*>
                collectionListModel.remove(jbTable.selectedIndex)
                modified = true
                jbTable.updateUI()
            }
            .setEditAction {
                val jbTable = it.contextComponent as JBList<*>

                CreateOrUpdateHostMachineDialogUI(settingState.hostMachines[jbTable.selectedIndex]) {
                    // do nothing.
                }.show()
            }
            .setAddAction {
                CreateOrUpdateHostMachineDialogUI(null) { state ->
                    @Suppress("UNCHECKED_CAST")
                    val jbTable = it.contextComponent as JBList<HostMachineConfigV2>
                    collectionListModel.add(state)
                    modified = true
                    jbTable.updateUI()
                }.show()
            }
            .disableUpDownActions()
            .setToolbarPosition(ActionToolbarPosition.TOP)
        return toolbarDecorator.createPanel()
    }

    override fun dispose() {
    }

}