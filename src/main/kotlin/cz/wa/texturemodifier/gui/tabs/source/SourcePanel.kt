package cz.wa.texturemodifier.gui.tabs.source

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane

class SourcePanel(val contentHolder: ContentHolder) : JPanel() {

    private val toolPanel = ToolPanel(contentHolder)
    private val canvas = SourceViewer(contentHolder, toolPanel)

    init {
        maximumSize = Dimension(120, 4096)
        layout = BorderLayout()
        val split = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        add(split)
        split.rightComponent = toolPanel
        split.leftComponent = canvas
        split.dividerLocation = 550
    }

    class ToolPanel(val contentHolder: ContentHolder) : JPanel() {
        val labelW = JLabel();
        val labelH = JLabel();

        init {
            add(labelW)
            add(labelH)
            updateSize(File(""))

            val fol = object : MainFrame.FileOpenListener {
                override fun fileOpened(file: File) {
                    updateSize(file)
                }
            }
            MainFrame.instance!!.addImageOpenListener(fol)
        }

        private fun updateSize(file: File) {
            labelW.text = "Size x: ${contentHolder.sourceImage!!.width}"
            labelH.text = "Size y: ${contentHolder.sourceImage!!.height}"
        }
    }
}