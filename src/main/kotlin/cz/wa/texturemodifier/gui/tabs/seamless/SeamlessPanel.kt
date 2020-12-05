package cz.wa.texturemodifier.gui.tabs.seamless

import cz.wa.texturemodifier.command.SeamlessCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class SeamlessPanel(contentHolder: ContentHolder) :
    AbstractPanel<SeamlessViewer>(contentHolder, SeamlessViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: SeamlessViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: SeamlessViewer) :
        AbstractToolPanel<SeamlessViewer>(contentHolder, canvas, 150, SeamlessCommand::class.java) {

        val distTf = JTextField()
        val alphaCb = JCheckBox("Alpha blending")

        init {
            // help
            add(createHelpButton())

            // distance
            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Distance PX"))

            distTf.text = contentHolder.settings.seamlessDist.toString()
            distTf.columns = 4
            p1.add(distTf)

            add(p1)

            // alpha
            alphaCb.isSelected = contentHolder.settings.seamlessAlpha
            add(alphaCb)

            // apply
            add(createApplyButton())
        }

        override fun applySettings() {
            contentHolder.settings.seamlessDist = distTf.text.toInt()
            contentHolder.settings.seamlessAlpha = alphaCb.isSelected
        }
    }
}