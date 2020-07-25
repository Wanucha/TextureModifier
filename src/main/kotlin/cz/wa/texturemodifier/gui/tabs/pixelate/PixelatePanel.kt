package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.ScaleType
import cz.wa.texturemodifier.command.PixelateCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class PixelatePanel(val contentHolder: ContentHolder) : JPanel() {
    private val canvas = PixelateViewer(contentHolder)
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
        split.dividerLocation = 530
    }

    class ToolPanel(val contentHolder: ContentHolder, val canvas: PixelateViewer) : JPanel() {
        val scaleTf = JTextField()
        val colorsTf = JTextField()
        val typeCb = JComboBox<ScaleType>(ScaleType.values())
        val toleranceTf = JTextField()
        val ignoreBgCb = JCheckBox("Ignore BG color")
        val bgColorTf = JTextField("#000000")

        init {
            maximumSize = Dimension(270, 4096)

            // scale
            scaleTf.text = contentHolder.settings.pixelateScale.toString()
            scaleTf.columns = 2
            add(GuiUtils.createValuePanel("Scale down", scaleTf))

            // colors
            colorsTf.text = contentHolder.settings.pixelateColors.toString()
            colorsTf.columns = 3
            add(GuiUtils.createValuePanel("Colors per channel", colorsTf))

            // scale type
            typeCb.isEditable = false
            typeCb.selectedItem = contentHolder.settings.pixelateScaleType
            add(GuiUtils.createValuePanel("Colors per channel", typeCb))

            // tolerance
            toleranceTf.text = contentHolder.settings.pixelateScaleColorTolerance.toString()
            toleranceTf.columns = 3
            add(GuiUtils.createValuePanel("Scale color tolerance", toleranceTf))

            // ignore bg color
            ignoreBgCb.isSelected = contentHolder.settings.pixelateIgnoreBgColor
            add(GuiUtils.createValuePanel(null, ignoreBgCb))

            // bg color
            bgColorTf.text = ColorUtils.toString(contentHolder.settings.pixelateBgColor)
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
            contentHolder.settings.pixelateScale = scaleTf.text.toInt()
            contentHolder.settings.pixelateColors = colorsTf.text.toInt()
            contentHolder.settings.pixelateScaleType = typeCb.selectedItem as ScaleType
            contentHolder.settings.pixelateScaleColorTolerance = toleranceTf.text.toInt()
            contentHolder.settings.pixelateIgnoreBgColor = ignoreBgCb.isSelected
            contentHolder.settings.pixelateBgColor = ColorUtils.parse(bgColorTf.text)
            contentHolder.outputImage = PixelateCommand(contentHolder.settings).execute(contentHolder.sourceImage!!)
            canvas.refresh()
        }
    }
}