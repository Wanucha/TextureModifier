package cz.wa.texturemodifier.gui.tabs.multiplycolor

import cz.wa.texturemodifier.command.MultiplyColorCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.ColorUtils
import javax.swing.JTextField

open class MultiplyColorPanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierViewer>(contentHolder, ModifierViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierViewer) :
        AbstractToolPanel<ModifierViewer>(contentHolder, canvas, 200, MultiplyColorCommand::class.java) {
        private val mulColorTf = JTextField("#FFFFFF")
        private val addColorTf = JTextField("#000000")

        init {
            // help
            add(createHelpButton())
            add(createEmptyPanel(40))

            // multiply
            mulColorTf.columns = 7
            add(GuiUtils.createValuePanel("Multiply color", mulColorTf))

            // add
            addColorTf.columns = 7
            add(GuiUtils.createValuePanel("Add color", addColorTf))
            // apply
            add(createApplyButton())

            showSettings()
        }

        override fun showSettings() {
            mulColorTf.text = ColorUtils.toString(contentHolder.settings.multiplyColorMulColor)
            addColorTf.text = ColorUtils.toString(contentHolder.settings.multiplyColorAddColor)
        }

        override fun applySettings() {
            contentHolder.settings.multiplyColorMulColor = ColorUtils.parse(mulColorTf.text)
            contentHolder.settings.multiplyColorAddColor = ColorUtils.parse(addColorTf.text)
        }
    }
}