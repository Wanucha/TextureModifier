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

class SourcePanel(val contentHolder: ContentHolder) : JPanel() {

    private val canvas = SourceViewer(contentHolder)
    private val toolPanel = ToolPanel(contentHolder, canvas)

    init {
        this@SourcePanel.layout = BorderLayout()
        MainFrame.instance!!.bgColorSlider.addListener { canvas.refresh() }
        add(canvas, BorderLayout.CENTER)
        add(toolPanel, BorderLayout.EAST)
    }

    class ToolPanel(val contentHolder: ContentHolder, val canvas: SourceViewer) : JPanel() {
        private val labelW = JLabel()
        private val labelH = JLabel()

        init {
            maximumSize = Dimension(200, 4096)
            preferredSize = maximumSize

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
            b1.toolTipText = "Takes last modified image as new source"
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
