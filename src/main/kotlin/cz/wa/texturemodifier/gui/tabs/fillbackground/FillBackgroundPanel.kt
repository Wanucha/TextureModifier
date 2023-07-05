package cz.wa.texturemodifier.gui.tabs.fillbackground

import cz.wa.texturemodifier.command.FillBackgroundCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import javax.swing.JCheckBox
import javax.swing.JTextField

open class FillBackgroundPanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierViewer>(contentHolder, ModifierViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierViewer) =
        ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierViewer) :
        AbstractToolPanel<ModifierViewer>(contentHolder, canvas, 200, FillBackgroundCommand::class.java) {

        private val iterationsTf = JTextField()
        private val includeCornersCb = JCheckBox("Fill color include corner pixels")
        private val averageFillCb = JCheckBox("Fill color average near pixels")
        private val bgColorTf = JTextField("#000000")

        init {
            // help
            add(createHelpButton())
            add(createEmptyPanel(40))

            // scale
            iterationsTf.columns = 2
            add(GuiUtils.createValuePanel("Iterations", iterationsTf))

            // include corners
            add(GuiUtils.createValuePanel(null, includeCornersCb))

            // average fill
            add(GuiUtils.createValuePanel(null, averageFillCb))

            // bg color
            bgColorTf.columns = 7
            add(GuiUtils.createValuePanel("BG color", bgColorTf))

            // apply
            add(createApplyButton())

            showSettings()
        }

        override fun showSettings() {
            iterationsTf.text = contentHolder.settings.fillBgIterations.toString()
            includeCornersCb.isSelected = contentHolder.settings.fillBgIncludeCorners
            averageFillCb.isSelected = contentHolder.settings.fillBgAverageFill
            bgColorTf.text = ColorUtils.toString(contentHolder.settings.fillBgBgColor)
        }

        override fun applySettings() {
            contentHolder.settings.fillBgIterations = iterationsTf.text.toInt()
            contentHolder.settings.fillBgIncludeCorners = includeCornersCb.isSelected
            contentHolder.settings.fillBgAverageFill = averageFillCb.isSelected
            contentHolder.settings.fillBgBgColor = ColorUtils.parse(bgColorTf.text)
        }
    }
}