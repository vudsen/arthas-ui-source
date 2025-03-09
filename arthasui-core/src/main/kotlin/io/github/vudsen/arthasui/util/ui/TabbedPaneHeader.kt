package io.github.vudsen.arthasui.util.ui

import com.intellij.ide.plugins.newui.TabHeaderListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import org.jetbrains.annotations.Nls
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.plaf.TabbedPaneUI

/**
 * @see com.intellij.ide.plugins.newui.TabbedPaneHeaderComponent
 */
class TabbedPaneHeader(actions: ActionGroup, listener: TabHeaderListener) : JPanel(BorderLayout()), Disposable {

    private val tabHeight = 36;

    // TODO change underline color when focus and leave. May check [com.intellij.openapi.wm.IdeFocusManager]
    private var isFocused = false


    private val myTabbedPane: JBTabbedPane = object : JBTabbedPane() {
        override fun setUI(ui: TabbedPaneUI) {
            val value = UIManager.getBoolean("TabbedPane.contentOpaque")
            val defaults = UIManager.getDefaults()
            defaults["TabbedPane.contentOpaque"] = java.lang.Boolean.FALSE
            defaults["TabbedPane.tabHeight"] = tabHeight
            if (isFocused) {
                defaults["TabbedPane.underlineColor"] = JBUI.CurrentTheme.TabbedPane.ENABLED_SELECTED_COLOR
            } else {
                defaults["TabbedPane.underlineColor"] = JBUI.CurrentTheme.TabbedPane.DISABLED_SELECTED_COLOR
            }
            try {
                super.setUI(ui)
            } finally {
                UIManager.getDefaults()["TabbedPane.contentOpaque"] = value
            }
        }
    }

    init {
        val createToolbar = createToolbar(actions)
        createToolbar.targetComponent = myTabbedPane
        add(createToolbar.component, BorderLayout.WEST)
        add(myTabbedPane, BorderLayout.CENTER)

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                println("CLICKED")
            }
        })
        isOpaque = false
        myTabbedPane.isOpaque = false
        myTabbedPane.preferredHeight = tabHeight
        myTabbedPane.addChangeListener { listener.selectionChanged(myTabbedPane.selectedIndex) }
    }

    companion object {
        private fun createToolbar(actionGroup: ActionGroup): ActionToolbar {
            val toolbar =
                ActionManager.getInstance().createActionToolbar(ActionPlaces.RUNNER_TOOLBAR, actionGroup, true)
            return toolbar
        }
    }

    fun addTab(title: @Nls String, icon: Icon?) {
        myTabbedPane.addTab(title, icon, JLabel())
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    fun setIsFocused(isFocused: Boolean) {
        this.isFocused = isFocused
        myTabbedPane.updateUI()
    }

}