package io.github.vudsen.arthasui.common.ui

import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.layout.ComponentPredicate
import javax.swing.JCheckBox

class CheckBoxPredicate(private val checkBox: JCheckBox, private val initialValue: Boolean) : ComponentPredicate() {

    private val listeners = mutableListOf<(Boolean) -> Unit>()

    constructor(checkBoxCell: Cell<JCheckBox>, initialValue: Boolean) : this(checkBoxCell.component, initialValue)

    init {
        checkBox.addChangeListener {
            for (listener in listeners) {
                listener(checkBox.isSelected)
            }
        }
    }

    override fun addListener(listener: (Boolean) -> Unit) {
        listeners.add(listener)
    }

    override fun invoke(): Boolean {
        return initialValue
    }

}