package cz.wa.texturemodifier.gui.tabs.blur

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

        val threshholdTf = JTextField()

        init {
            // help
            add(createHelpButton())

            // threshhold
            threshholdTf.text = contentHolder.settings.removeAlphaThreshhold.toString()
            threshholdTf.columns = 3
            add(GuiUtils.createValuePanel("Threshhold", threshholdTf))

            // apply
            add(createApplyButton())
        }

        override fun applySettings() {
            contentHolder.settings.removeAlphaThreshhold = threshholdTf.text.toInt()
        }
    }
}