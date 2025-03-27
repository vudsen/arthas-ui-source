package io.github.vudsen.arthasui.common.validation

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.ui.layout.ValidationInfoBuilder
import javax.swing.text.JTextComponent

class TextComponentValidators : DialogValidation.WithParameter<JTextComponent> {

    companion object {
        private class MyValidation(private val component: JTextComponent) : DialogValidation {
            override fun validate(): ValidationInfo? {
                if (!component.isEnabled) {
                    return null
                }
                if (component.text.isEmpty()) {
                    return ValidationInfoBuilder(component).error("Should not be empty.")
                }
                return null
            }
        }
    }

    override fun curry(parameter: JTextComponent): DialogValidation {
        return MyValidation(parameter)
    }

}
