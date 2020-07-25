package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.command.BlurCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.utils.GuiUtils
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*

class BlurPanel(val contentHolder: ContentHolder) : JPanel() {
    private val canvas = BlurViewer(contentHolder)
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

    class ToolPanel(val contentHolder: ContentHolder, val canvas: BlurViewer) : JPanel() {
        val radiusTf = JTextField()
        val ratioTf = JTextField()

        init {
            maximumSize = Dimension(120, 4096)

            // radius
            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Radius"))

            radiusTf.text = contentHolder.settings.blurRadius.toString()
            radiusTf.columns = 6
            p1.add(radiusTf)

            add(p1)

            // ratio
            val p2 = JPanel(FlowLayout())

            p2.add(JLabel("Ratio"))

            ratioTf.text = contentHolder.settings.blurRatio.toString()
            ratioTf.columns = 6
            p2.add(ratioTf)

            add(p2)

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
            contentHolder.settings.blurRadius = radiusTf.text.toDouble()
            contentHolder.settings.blurRatio = ratioTf.text.toDouble()
            contentHolder.outputImage = BlurCommand(contentHolder.settings).execute(contentHolder.sourceImage!!)
            canvas.refresh()
        }
    }
}