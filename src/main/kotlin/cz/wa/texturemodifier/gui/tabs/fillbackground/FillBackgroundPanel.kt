package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.command.FillBackgroundCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import javax.swing.JCheckBox
import javax.swing.JTextField

class FillBackgroundPanel(contentHolder: ContentHolder) :
    AbstractPanel<FillBackgroundViewer>(contentHolder, FillBackgroundViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: FillBackgroundViewer) =
        ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: FillBackgroundViewer) :
        AbstractToolPanel<FillBackgroundViewer>(contentHolder, canvas, 200, FillBackgroundCommand::class.java) {

        val iterationsTf = JTextField()
        val includeCornersCb = JCheckBox("Fill color include corner pixels")
        val averageFillCb = JCheckBox("Fill color average near pixels")
        val bgColorTf = JTextField("#000000")

        init {
            // help
            add(createHelpButton())
            add(createEmptyPanel(40))

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
            add(createApplyButton())
        }

        override fun applySettings() {
            contentHolder.settings.fillBgIterations = iterationsTf.text.toInt()
            contentHolder.settings.fillBgIncludeCorners = includeCornersCb.isSelected
            contentHolder.settings.fillBgAverageFill = averageFillCb.isSelected
            contentHolder.settings.fillBgBgColor = ColorUtils.parse(bgColorTf.text)
        }
    }
}