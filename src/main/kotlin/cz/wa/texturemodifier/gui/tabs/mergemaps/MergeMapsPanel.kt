package cz.wa.texturemodifier.gui.tabs.mergemaps

import cz.wa.texturemodifier.MapType
import cz.wa.texturemodifier.command.MergeMapCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierAlphaViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import javax.swing.JComboBox
import javax.swing.JTextField

open class MergeMapsPanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierAlphaViewer>(contentHolder, ModifierAlphaViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierAlphaViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierAlphaViewer) :
        AbstractToolPanel<ModifierAlphaViewer>(contentHolder, canvas, 240, MergeMapCommand::class.java) {

        private val layoutCb = JComboBox(MapType.values())
        private val map1Tf = JTextField()
        private val map2Tf = JTextField()
        private val map3Tf = JTextField()
        private val map4Tf = JTextField()

        init {
            // apply
            add(createHelpButton())

            // type
            layoutCb.isEditable = false
            add(GuiUtils.createValuePanel("Input maps layout", layoutCb))

            // maps
            map1Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 1 (upper left)", map1Tf))

            map2Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 2 (ur/ll)", map2Tf))

            map3Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 3 (lower left)", map3Tf))

            map4Tf.columns = 9
            add(GuiUtils.createValuePanel("Map 4 (lower right)", map4Tf))

            // apply
            add(createApplyButton())

            showSettings()
        }

        override fun showSettings() {
            layoutCb.selectedItem = contentHolder.settings.mergeMapsLayout
            map1Tf.text = contentHolder.settings.mergeMapsMap1
            map2Tf.text = contentHolder.settings.mergeMapsMap2
            map3Tf.text = contentHolder.settings.mergeMapsMap3
            map4Tf.text = contentHolder.settings.mergeMapsMap4
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