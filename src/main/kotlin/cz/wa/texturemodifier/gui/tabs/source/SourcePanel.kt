package cz.wa.texturemodifier.gui.tabs.source

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.utils.ImageUtils
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.io.File
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane

class SourcePanel(val contentHolder: ContentHolder) : JPanel() {

    private val canvas = SourceViewer(contentHolder)
    private val toolPanel = ToolPanel(contentHolder, canvas)

    init {
        maximumSize = Dimension(120, 4096)
        layout = BorderLayout()
        val split = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        add(split)
        split.rightComponent = toolPanel
        split.leftComponent = canvas
        split.dividerLocation = 550
    }

    class ToolPanel(val contentHolder: ContentHolder, val canvas: SourceViewer) : JPanel() {
        val labelW = JLabel();
        val labelH = JLabel();

        init {
            // size
            val p1 = JPanel(FlowLayout())
            p1.add(labelW)
            p1.add(labelH)
            add(p1)

            // button
            val b1 = JButton("Apply modified")
            b1.addActionListener{
                applyModified()
            }
            add(b1)

            updateSize(File(""))

            val fol = object : MainFrame.FileOpenListener {
                override fun fileOpened(file: File) {
                    updateSize(file)
                }
            }
            MainFrame.instance!!.addImageOpenListener(fol)
        }

        private fun applyModified() {
            contentHolder.sourceImage = contentHolder.outputImage
            contentHolder.outputImage = ImageUtils.copyImage(contentHolder.sourceImage!!)
            canvas.refresh()
        }

        private fun updateSize(file: File) {
            labelW.text = "Size x: ${contentHolder.sourceImage!!.width}"
            labelH.text = "Size y: ${contentHolder.sourceImage!!.height}"
        }
    }
}