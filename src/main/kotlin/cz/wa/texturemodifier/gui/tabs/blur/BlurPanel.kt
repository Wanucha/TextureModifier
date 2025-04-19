package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.command.BlurCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.ModifierViewer
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

open class BlurPanel(contentHolder: ContentHolder) :
    AbstractPanel<ModifierViewer>(contentHolder, ModifierViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: ModifierViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: ModifierViewer) :
        AbstractToolPanel<ModifierViewer>(contentHolder, canvas, 150, BlurCommand::class.java) {

        private val radiusTf = JTextField()
        private val ratioTf = JTextField()

        init {
            // apply
            add(createHelpButton())

            // radius
            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Radius"))

            radiusTf.columns = 6
            p1.add(radiusTf)

            add(p1)

            // ratio
            val p2 = JPanel(FlowLayout())

            p2.add(JLabel("Ratio"))

            ratioTf.columns = 6
            p2.add(ratioTf)

            add(p2)

            // apply
            add(createApplyButton())

            showSettings()
        }

        override fun showSettings() {
            with (contentHolder.settings.blur) {
                radius.toString()
                ratio.toString()
            }
        }

        override fun applySettings() {
            with (contentHolder.settings.blur) {
                radius = radiusTf.text.toDouble()
                ratio = ratioTf.text.toDouble()
            }
        }
    }
}
