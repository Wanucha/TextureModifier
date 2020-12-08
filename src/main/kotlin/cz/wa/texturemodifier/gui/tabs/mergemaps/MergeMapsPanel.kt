package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.MapType
import cz.wa.texturemodifier.command.MergeMapCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierAlphaViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import javax.swing.JComboBox
import javax.swing.JTextField

class MergeMapsPanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierAlphaViewer>(contentHolder, ModifierAlphaViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierAlphaViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierAlphaViewer) :
        AbstractToolPanel<ModifierAlphaViewer>(contentHolder, canvas, 240, MergeMapCommand::class.java) {

        val layoutCb = JComboBox<MapType>(MapType.values())
        val map1Tf = JTextField()
        val map2Tf = JTextField()
        val map3Tf = JTextField()
        val map4Tf = JTextField()

        init {
            // apply
            add(createHelpButton())

            // type
            layoutCb.isEditable = false
            layoutCb.selectedItem = contentHolder.settings.mergeMapsLayout
            add(GuiUtils.createValuePanel("Input maps layout", layoutCb))

            // maps
            map1Tf.text = contentHolder.settings.mergeMapsMap1
            map1Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 1 (upper left)", map1Tf))

            map2Tf.text = contentHolder.settings.mergeMapsMap2
            map2Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 2 (ur/ll)", map2Tf))

            map3Tf.text = contentHolder.settings.mergeMapsMap3
            map3Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 3 (lower left)", map3Tf))

            map4Tf.text = contentHolder.settings.mergeMapsMap4
            map4Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 4 (lower right)", map4Tf))

            // apply
            add(createApplyButton())
        }

        override fun applySettings() {
            contentHolder.settings.mergeMapsLayout = layoutCb.selectedItem as MapType
            contentHolder.settings.mergeMapsMap1 = map1Tf.text
            contentHolder.settings.mergeMapsMap2 = map2Tf.text
            contentHolder.settings.mergeMapsMap3 = map3Tf.text
            contentHolder.settings.mergeMapsMap4 = map4Tf.text
        }
    }
}