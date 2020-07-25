package cz.wa.texturemodifier.gui.tabs.seamless

import cz.wa.texturemodifier.command.SeamlessCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.utils.GuiUtils
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
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
        val distTf = JTextField()
        val alphaCb = JCheckBox("Alpha blending")

        init {
            maximumSize = Dimension(120, 4096)

            // distance
            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Distance PX"))

            distTf.text = contentHolder.settings.seamlessDist.toString()
            distTf.columns = 4
            p1.add(distTf)

            add(p1)

            // alpha
            alphaCb.isSelected = contentHolder.settings.seamlessAlpha
            add(alphaCb)

            // apply
            val applyB = JButton("Apply")
            add(applyB)
            applyB.addActionListener {
                GuiUtils.runCatch(this, Runnable {
                    apply()
                })
            }
        }

        private fun apply() {
            contentHolder.settings.seamlessDist = distTf.text.toInt()
            contentHolder.settings.seamlessAlpha = alphaCb.isSelected
            contentHolder.outputImage = SeamlessCommand(contentHolder.settings).execute(contentHolder.sourceImage!!)
            canvas.refresh()
        }
    }
}