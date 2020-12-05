package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.MapType
import cz.wa.texturemodifier.command.MergeMapCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.utils.GuiUtils
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.JTextField

class MergeMapsPanel(val contentHolder: ContentHolder) : JPanel() {
    private val canvas = MergeMapsViewer(contentHolder)
    private val toolPanel = ToolPanel(contentHolder, canvas)

    init {
        initComponents()
    }

    private fun initComponents() {
        layout = BorderLayout()
        add(canvas, BorderLayout.CENTER)
        add(toolPanel, BorderLayout.EAST)
    }

    /**
     * UI Panel
     */
    class ToolPanel(val contentHolder: ContentHolder, val canvas: MergeMapsViewer) : JPanel() {
        val layoutCb = JComboBox<MapType>(MapType.values())
        val map1Tf = JTextField()
        val map2Tf = JTextField()
        val map3Tf = JTextField()
        val map4Tf = JTextField()

        init {
            maximumSize = Dimension(240, 4096)
            preferredSize = maximumSize

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
            val applyB = JButton("Apply")
            add(applyB)
            applyB.addActionListener {
                GuiUtils.runCatch(this, Runnable {
                    apply()
                })
            }
        }

        private fun apply() {
            contentHolder.settings.mergeMapsLayout = layoutCb.selectedItem as MapType
            contentHolder.settings.mergeMapsMap1 = map1Tf.text
            contentHolder.settings.mergeMapsMap2 = map2Tf.text
            contentHolder.settings.mergeMapsMap3 = map3Tf.text
            contentHolder.settings.mergeMapsMap4 = map4Tf.text
            contentHolder.outputImage = MergeMapCommand(contentHolder.settings).execute(contentHolder.sourceImage!!)
            canvas.refresh()
        }
    }
}