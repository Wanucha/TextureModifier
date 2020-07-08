package cz.wa.texturemodifier.gui.tabs.seamless

import cz.wa.texturemodifier.command.SeamlessCommand
import cz.wa.texturemodifier.gui.ContentHolder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.text.NumberFormat
import javax.swing.*

class SeamlessPanel(val contentHolder: ContentHolder) : JPanel() {
    private val canvas = SeamlessViewer(contentHolder)
    private val toolPanel = ToolPanel(contentHolder, canvas)

    init {
        initComponents()
    }

    private fun initComponents() {
        layout = BorderLayout()
        val split = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        add(split)
        split.rightComponent = toolPanel
        split.leftComponent = canvas
        split.dividerLocation = 650
    }

    class ToolPanel(val contentHolder: ContentHolder, val canvas: SeamlessViewer) : JPanel() {
        val distTf = JFormattedTextField(NumberFormat.getNumberInstance())
        val alphaCb = JCheckBox("Alpha blending")

        init {
            maximumSize = Dimension(120, 4096)

            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Distance PX"))

            distTf.value = contentHolder.settings.seamlessDist
            distTf.columns = 4
            p1.add(distTf)

            add(p1)

            alphaCb.isSelected = contentHolder.settings.seamlessAlpha
            add(alphaCb)

            val applyB = JButton("Apply")
            add(applyB)
            applyB.addActionListener{
                apply()
            }
        }

        private fun apply() {
            contentHolder.settings.seamlessDist = (distTf.value as Number).toInt()
            contentHolder.settings.seamlessAlpha = alphaCb.isSelected
            contentHolder.outputImage = SeamlessCommand(contentHolder.settings).execute(contentHolder.sourceImage!!)
            canvas.refresh()
        }
    }
}