package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.command.FillBackgroundCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

class FillBackgroundPanel(val contentHolder: ContentHolder) : JPanel() {
    private val canvas = FillBackgroundViewer(contentHolder)
    private val toolPanel = ToolPanel(contentHolder, canvas)

    init {
        initComponents()
    }

    private fun initComponents() {
        layout = BorderLayout()
        add(canvas, BorderLayout.CENTER)
        add(toolPanel, BorderLayout.EAST)
    }

    /**
     * UI Panel
     */
    class ToolPanel(val contentHolder: ContentHolder, val canvas: FillBackgroundViewer) : JPanel() {
        val iterationsTf = JTextField()
        val includeCornersCb = JCheckBox("Fill color include corner pixels")
        val averageFillCb = JCheckBox("Fill color average near pixels")
        val bgColorTf = JTextField("#000000")

        init {
            maximumSize = Dimension(200, 4096)
            preferredSize = maximumSize

            // scale
            iterationsTf.text = contentHolder.settings.fillBgIterations.toString()
            iterationsTf.columns = 2
            add(GuiUtils.createValuePanel("Iterations", iterationsTf))

            // include corners
            includeCornersCb.isSelected = contentHolder.settings.fillBgIncludeCorners
            add(GuiUtils.createValuePanel(null, includeCornersCb))

            // average fill
            averageFillCb.isSelected = contentHolder.settings.fillBgAverageFill
            add(GuiUtils.createValuePanel(null, averageFillCb))

            // bg color
            bgColorTf.text = ColorUtils.toString(contentHolder.settings.fillBgBgColor)
            bgColorTf.columns = 7
            add(GuiUtils.createValuePanel("BG color", bgColorTf))

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
            contentHolder.settings.fillBgIterations = iterationsTf.text.toInt()
            contentHolder.settings.fillBgIncludeCorners = includeCornersCb.isSelected
            contentHolder.settings.fillBgAverageFill = averageFillCb.isSelected
            contentHolder.settings.fillBgBgColor = ColorUtils.parse(bgColorTf.text)
            contentHolder.outputImage = FillBackgroundCommand(contentHolder.settings).execute(contentHolder.sourceImage!!)
            canvas.refresh()
        }
    }
}