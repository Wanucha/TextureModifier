package cz.wa.texturemodifier.gui.utils

import java.awt.Component
import java.text.NumberFormat
import java.util.*
import javax.swing.JFormattedTextField
import javax.swing.JOptionPane

object GuiUtils {
    fun showError(parent: Component, content: Any) {
        JOptionPane.showMessageDialog(parent, content, "Error", JOptionPane.ERROR_MESSAGE)
    }

    fun runCatch(parent: Component, l: Runnable) {
        try {
            l.run()
        } catch (e: Throwable) {
            e.printStackTrace()
            showError(parent, e)
        }
    }

    fun createNumTextField() = JFormattedTextField(NumberFormat.getInstance(Locale.US))
}