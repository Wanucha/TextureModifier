package cz.wa.texturemodifier.gui.tabs.removealpha

import cz.wa.texturemodifier.command.RemoveAlphaCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierAlphaViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import javax.swing.JTextField

class RemoveAlphaPanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierAlphaViewer>(contentHolder, ModifierAlphaViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierAlphaViewer) =
        ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierAlphaViewer) :
        AbstractToolPanel<ModifierAlphaViewer>(contentHolder, canvas, 130, RemoveAlphaCommand::class.java) {

        private val thresholdTf = JTextField()

        init {
            // help
            add(createHelpButton())

            // threshold
            thresholdTf.text = contentHolder.settings.removeAlphaThreshold.toString()
            thresholdTf.columns = 3
            add(GuiUtils.createValuePanel("Threshold", thresholdTf))

            // apply
            add(createApplyButton())
        }

        override fun applySettings() {
            contentHolder.settings.removeAlphaThreshold = thresholdTf.text.toInt()
        }
    }
}