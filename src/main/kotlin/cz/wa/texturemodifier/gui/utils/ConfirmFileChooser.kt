package cz.wa.texturemodifier.gui.utils

import javax.swing.JFileChooser
import javax.swing.JOptionPane

class ConfirmFileChooser: JFileChooser() {
    override fun approveSelection() {
        val f = selectedFile
        if (f.isFile() && dialogType == JFileChooser.SAVE_DIALOG) {
            val result = JOptionPane.showConfirmDialog(
                this,
                "File ${f.absolutePath} already exists, overwrite?",
                "Existing file",
                JOptionPane.YES_NO_CANCEL_OPTION
            )
            when (result) {
                JOptionPane.YES_OPTION -> {
                    super.approveSelection()
                    return
                }
                JOptionPane.NO_OPTION -> return
                JOptionPane.CLOSED_OPTION -> return
                JOptionPane.CANCEL_OPTION -> {
                    cancelSelection()
                    return
                }
            }
        }
        super.approveSelection()
    }
}