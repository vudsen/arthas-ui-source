package io.github.vudsen.arthasui.util.ui

import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics
import java.awt.geom.RoundRectangle2D
import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JTextPane


class TagLabel(text: String, color: Color = JBColor.LIGHT_GRAY) : JTextPane() {

    init {
        this.text = text
        background = color
        foreground = JBColor.WHITE
        border = createEmptyBorder(0, 10, 0, 10)
    }


    override fun paint(g: Graphics) {
        val fieldX = 0.0;
        val fieldY = 0.0;
        val fieldWeight = size.width;
        val fieldHeight = size.height;
        g.clip = RoundRectangle2D.Double(fieldX, fieldY, fieldWeight.toDouble(), fieldHeight.toDouble(), 15.0, 15.0);
        super.paint(g);
    }


}