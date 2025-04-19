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
            with (contentHolder.settings.fillBackground) {
                iterationsTf.text = iterations.toString()
                includeCornersCb.isSelected = includeCorners
                averageFillCb.isSelected = averageFill
                bgColorTf.text = ColorUtils.toString(bgColor)
            }
        }

        override fun applySettings() {
            with (contentHolder.settings.fillBackground) {
                iterations = iterationsTf.text.toInt()
                includeCorners = includeCornersCb.isSelected
                averageFill = averageFillCb.isSelected
                bgColor = ColorUtils.parse(bgColorTf.text)
            }
        }
    }
}
