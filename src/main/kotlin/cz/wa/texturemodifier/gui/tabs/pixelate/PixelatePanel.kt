package cz.wa.texturemodifier.gui.tabs.pixelate

import cz.wa.texturemodifier.ScaleType
import cz.wa.texturemodifier.SmoothType
import cz.wa.texturemodifier.command.PixelateCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Component
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
        private val middleUseCb = JCheckBox("Use middle step")
        private val middleScaleTf = JTextField()
        private val colorsTf = JTextField()
        private val typeCb = JComboBox(ScaleType.values())
        private val toleranceTf = JTextField()
        private val ignoreBgCb = JCheckBox("Ignore BG color")
        private val bgColorTf = JTextField("#000000")
        private val blendSmoothTf = JTextField()
        private val smoothTypeCb = JComboBox(SmoothType.values())

        private val panelType = JPanel(FlowLayout())
        private val colorComponents = mutableListOf<Component>()

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
            useSizeCb.addChangeListener {onUseSizeChanged()}
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

            // middle step
            val pm = JPanel(FlowLayout())
            pm.border = BorderFactory.createTitledBorder("Middle step")
            pm.maximumSize = Dimension(200, 150)
            pm.preferredSize = Dimension(200, 100)

            middleUseCb.addChangeListener {onMiddleUseChanged()}
            pm.add(middleUseCb)

            middleScaleTf.columns = 3
            pm.add(GuiUtils.createValuePanel("Scale of target", middleScaleTf))

            add(pm)

            // colors
            colorsTf.columns = 3
            p2.add(GuiUtils.createValuePanel("Colors per channel", colorsTf))

            add(p2)

            panelType.border = BorderFactory.createTitledBorder("Pixelate")
            panelType.maximumSize = Dimension(200, 200)
            panelType.minimumSize = Dimension(200, 40)
            panelType.preferredSize = Dimension(200, 170)

            // scale type
            typeCb.isEditable = false
            panelType.add(GuiUtils.createValuePanel("Scale filter", typeCb))
            typeCb.addActionListener {onTypeChanged()}

            // tolerance
            toleranceTf.columns = 3
            GuiUtils.createValuePanel("Scale color tolerance", toleranceTf).let {
                panelType.add(it)
                colorComponents.add(it)
            }

            // ignore bg color
            GuiUtils.createValuePanel(null, ignoreBgCb).let {
                panelType.add(it)
                colorComponents.add(it)
            }

            // bg color
            bgColorTf.columns = 7
            GuiUtils.createValuePanel("BG color", bgColorTf).let {
                panelType.add(it)
                colorComponents.add(it)

            }

            add(panelType)

            val p5 = JPanel(FlowLayout())
            p5.border = BorderFactory.createTitledBorder("Smooth")
            p5.maximumSize = Dimension(200, 150)
            p5.preferredSize = Dimension(200, 100)

            // smooth blend
            blendSmoothTf.columns = 4
            p5.add(GuiUtils.createValuePanel("Smooth blend ratio", blendSmoothTf))

            // smooth type
            smoothTypeCb.isEditable = false
            p5.add(GuiUtils.createValuePanel("Smooth filter", smoothTypeCb))

            add(p5)

            // apply
            add(createApplyButton())

            showSettings()
        }

        private fun onMiddleUseChanged() {
            middleScaleTf.isEnabled = middleUseCb.isSelected
        }

        private fun onTypeChanged() {
            val showColor = typeCb.selectedItem == ScaleType.MOST_COLOR
            colorComponents.forEach { it.isVisible = showColor }
            panelType.preferredSize = Dimension(190, if (showColor) 170 else 60)
        }

        private fun onUseSizeChanged() {
            val value = useSizeCb.isSelected
            scaleTf.isEnabled = !value
            sizeXTf.isEnabled = value
            sizeYTf.isEnabled = value
        }

        override fun showSettings() {
            with (contentHolder.settings) {
                scaleTf.text = pixelateScale.toString()
                useSizeCb.isSelected = pixelateUseSize
                sizeXTf.text = pixelateSizeX.toString()
                sizeYTf.text = pixelateSizeY.toString()
                middleUseCb.isSelected = pixelateMiddleUse
                middleScaleTf.text = pixelateMiddleScale.toString()
                colorsTf.text = pixelateColors.toString()
                typeCb.selectedItem = pixelateScaleType
                toleranceTf.text = pixelateScaleColorTolerance.toString()
                ignoreBgCb.isSelected = pixelateIgnoreBgColor
                bgColorTf.text = ColorUtils.toString(pixelateBgColor)
                blendSmoothTf.text = pixelateBlendSmooth.toString()
                smoothTypeCb.selectedItem = pixelateSmoothType
            }
        }

        override fun applySettings() {
            with (contentHolder.settings) {
                pixelateScale = scaleTf.text.toDouble()
                pixelateUseSize = useSizeCb.isSelected
                pixelateSizeX = sizeXTf.text.toInt()
                pixelateSizeY = sizeYTf.text.toInt()
                pixelateMiddleUse = middleUseCb.isSelected
                pixelateMiddleScale = middleScaleTf.text.toDouble()
                pixelateColors = colorsTf.text.toInt()
                pixelateScaleType = typeCb.selectedItem as ScaleType
                pixelateScaleColorTolerance = toleranceTf.text.toInt()
                pixelateIgnoreBgColor = ignoreBgCb.isSelected
                pixelateBgColor = ColorUtils.parse(bgColorTf.text)
                pixelateBlendSmooth = blendSmoothTf.text.toDouble()
                pixelateSmoothType = smoothTypeCb.selectedItem as SmoothType
            }
        }
    }
}
