package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.command.BlurCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class BlurPanel(contentHolder: ContentHolder) :
    AbstractPanel<BlurViewer>(contentHolder, BlurViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: BlurViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: BlurViewer) :
        AbstractToolPanel<BlurViewer>(contentHolder, canvas, 150, BlurCommand::class.java) {

        val radiusTf = JTextField()
        val ratioTf = JTextField()

        init {
            // apply
            add(createHelpButton())

            // radius
            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Radius"))

            radiusTf.text = contentHolder.settings.blurRadius.toString()
            radiusTf.columns = 6
            p1.add(radiusTf)

            add(p1)

            // ratio
            val p2 = JPanel(FlowLayout())

            p2.add(JLabel("Ratio"))

            ratioTf.text = contentHolder.settings.blurRatio.toString()
            ratioTf.columns = 6
            p2.add(ratioTf)

            add(p2)

            // apply
            add(createApplyButton())
        }

        override fun applySettings() {
            contentHolder.settings.blurRadius = radiusTf.text.toDouble()
            contentHolder.settings.blurRatio = ratioTf.text.toDouble()
        }
    }
}