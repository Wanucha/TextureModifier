package cz.wa.texturemodifier.gui.tabs.pixelate

import cz.wa.texturemodifier.ScaleType
import cz.wa.texturemodifier.SmoothType
import cz.wa.texturemodifier.command.PixelateCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import kotlin.math.roundToInt

open class PixelatePanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierViewer>(contentHolder, ModifierViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierViewer) :
        AbstractToolPanel<ModifierViewer>(contentHolder, canvas, 200, PixelateCommand::class.java) {
        private val scaleTf = JTextField()
        private val useSizeCb = JCheckBox("Use fixed size, not scale")
        private val sizeXTf = JTextField()
        private val sizeYTf = JTextField()
        private val colorsTf = JTextField()
        private val typeCb = JComboBox(ScaleType.values())
        private val toleranceTf = JTextField()
        private val ignoreBgCb = JCheckBox("Ignore BG color")
        private val bgColorTf = JTextField("#000000")
        private val blendSmoothTf = JTextField()
        private val smoothTypeCb = JComboBox(SmoothType.values())

        init {
            // help
            add(createHelpButton())
            add(createEmptyPanel(40))

            val p1 = JPanel(FlowLayout())
            p1.border = BorderFactory.createTitledBorder("Scale")
            p1.maximumSize = Dimension(200, 300)
            p1.preferredSize = Dimension(200, 200)

            // scale
            scaleTf.columns = 4
            p1.add(GuiUtils.createValuePanel("Scale down", scaleTf))
            val bApplyScale = JButton("Use")
            bApplyScale.addActionListener {
                val scale = scaleTf.text.toDouble()
                sizeXTf.text = (contentHolder.sourceImage!!.width / scale).roundToInt().toString()
                sizeYTf.text = (contentHolder.sourceImage!!.height / scale).roundToInt().toString()
            }
            bApplyScale.toolTipText = "Apply scale to current image size (just fill the next two fields)"
            p1.add(bApplyScale)

            // use scale
            useSizeCb.addChangeListener {useSizeChanged()}
            p1.add(GuiUtils.createValuePanel(null, useSizeCb))

            // size
            sizeXTf.columns = 4
            p1.add(GuiUtils.createValuePanel("Size X", sizeXTf))

            sizeYTf.columns = 4
            p1.add(GuiUtils.createValuePanel("Size Y", sizeYTf))

            add(p1)

            val p2 = JPanel(FlowLayout())
            p2.border = BorderFactory.createTitledBorder("Colors")
            p2.maximumSize = Dimension(200, 100)
            p2.preferredSize = Dimension(200, 60)

            // colors
            colorsTf.columns = 3
            p2.add(GuiUtils.createValuePanel("Colors per channel", colorsTf))

            add(p2)

            val p3 = JPanel(FlowLayout())
            p3.border = BorderFactory.createTitledBorder("Pixelate")
            p3.maximumSize = Dimension(200, 200)
            p3.preferredSize = Dimension(200, 170)

            // scale type
            typeCb.isEditable = false
            p3.add(GuiUtils.createValuePanel("Scale filter", typeCb))

            // tolerance
            toleranceTf.columns = 3
            p3.add(GuiUtils.createValuePanel("Scale color tolerance", toleranceTf))

            // ignore bg color
            p3.add(GuiUtils.createValuePanel(null, ignoreBgCb))

            // bg color
            bgColorTf.columns = 7
            p3.add(GuiUtils.createValuePanel("BG color", bgColorTf))

            add(p3)

            val p4 = JPanel(FlowLayout())
            p4.border = BorderFactory.createTitledBorder("Smooth")
            p4.maximumSize = Dimension(200, 150)
            p4.preferredSize = Dimension(200, 100)

            // smooth blend
            blendSmoothTf.columns = 4
            p4.add(GuiUtils.createValuePanel("Smooth blend ratio", blendSmoothTf))

            // smooth type
            smoothTypeCb.isEditable = false
            p4.add(GuiUtils.createValuePanel("Smooth filter", smoothTypeCb))

            add(p4)

            // apply
            add(createApplyButton())

            showSettings()
        }

        private fun useSizeChanged() {
            val value = useSizeCb.isSelected
            scaleTf.isEnabled = !value
            sizeXTf.isEnabled = value
            sizeYTf.isEnabled = value
        }

        override fun showSettings() {
            scaleTf.text = contentHolder.settings.pixelateScale.toString()
            useSizeCb.isSelected = contentHolder.settings.pixelateUseSize
            sizeXTf.text = contentHolder.settings.pixelateSizeX.toString()
            sizeYTf.text = contentHolder.settings.pixelateSizeY.toString()
            colorsTf.text = contentHolder.settings.pixelateColors.toString()
            typeCb.selectedItem = contentHolder.settings.pixelateScaleType
            toleranceTf.text = contentHolder.settings.pixelateScaleColorTolerance.toString()
            ignoreBgCb.isSelected = contentHolder.settings.pixelateIgnoreBgColor
            bgColorTf.text = ColorUtils.toString(contentHolder.settings.pixelateBgColor)
            blendSmoothTf.text = contentHolder.settings.pixelateBlendSmooth.toString()
            smoothTypeCb.selectedItem = contentHolder.settings.pixelateSmoothType
        }

        override fun applySettings() {
            contentHolder.settings.pixelateScale = scaleTf.text.toDouble()
            contentHolder.settings.pixelateUseSize = useSizeCb.isSelected
            contentHolder.settings.pixelateSizeX = sizeXTf.text.toInt()
            contentHolder.settings.pixelateSizeY = sizeYTf.text.toInt()
            contentHolder.settings.pixelateColors = colorsTf.text.toInt()
            contentHolder.settings.pixelateScaleType = typeCb.selectedItem as ScaleType
            contentHolder.settings.pixelateScaleColorTolerance = toleranceTf.text.toInt()
            contentHolder.settings.pixelateIgnoreBgColor = ignoreBgCb.isSelected
            contentHolder.settings.pixelateBgColor = ColorUtils.parse(bgColorTf.text)
            contentHolder.settings.pixelateBlendSmooth = blendSmoothTf.text.toDouble()
            contentHolder.settings.pixelateSmoothType = smoothTypeCb.selectedItem as SmoothType
        }
    }
}