package cz.wa.texturemodifier.gui.utils

import java.awt.Component
import java.awt.FlowLayout
import java.text.NumberFormat
import java.util.*
import javax.swing.*

object GuiUtils {
    private fun showError(parent: Component, content: Any) {
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

    fun createValuePanel(label: String?, component: JComponent): JPanel {
        val ret = JPanel(FlowLayout())
        if (label != null) {
            ret.add(JLabel(label))
        }
        ret.add(component)
        return ret
    }
}