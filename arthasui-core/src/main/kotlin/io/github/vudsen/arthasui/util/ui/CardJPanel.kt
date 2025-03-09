package io.github.vudsen.arthasui.util.ui

import java.awt.CardLayout
import javax.swing.JPanel

class CardJPanel : JPanel(CardLayout()) {


    override fun getLayout(): CardLayout {
        return super.getLayout() as CardLayout
    }
}