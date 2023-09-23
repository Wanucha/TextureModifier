package cz.wa.texturemodifier.gui.tabs.seamless

import cz.wa.texturemodifier.command.SeamlessCommand
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.tabs.AbstractPanel
import cz.wa.texturemodifier.gui.tabs.TillingMdofierViewer
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

open class SeamlessPanel(contentHolder: ContentHolder) :
    AbstractPanel<TillingMdofierViewer>(contentHolder, TillingMdofierViewer(contentHolder)) {

    override fun createPanel(contentHolder: ContentHolder, canvas: TillingMdofierViewer) = ToolPanel(contentHolder, canvas)

    protected class ToolPanel(contentHolder: ContentHolder, canvas: TillingMdofierViewer) :
        AbstractToolPanel<TillingMdofierViewer>(contentHolder, canvas, 150, SeamlessCommand::class.java) {

        private val distTf = JTextField()
        private val alphaCb = JCheckBox("Alpha blending")
        private val overlapCb = JCheckBox("Overlap (reduce size)")
        private val previewCb = JCheckBox("Preview tilling")

        init {
            // help
            add(createHelpButton())

            // distance
            val p1 = JPanel(FlowLayout())

            p1.add(JLabel("Distance PX"))

            distTf.columns = 4
            p1.add(distTf)

            add(p1)

            // alpha
            add(alphaCb)

            // overlap
            add(overlapCb)

            // apply
            add(createApplyButton())

            // preview
            previewCb.addChangeListener { togglePreviewChanged() }
            add(previewCb)

            showSettings()
        }

        private fun togglePreviewChanged() {
            canvas.showTilling = previewCb.isSelected
            canvas.refresh()
        }

        override fun showSettings() {
            distTf.text = contentHolder.settings.seamlessDist.toString()
            alphaCb.isSelected = contentHolder.settings.seamlessAlpha
            overlapCb.isSelected = contentHolder.settings.seamlessOverlap
        }

        override fun applySettings() {
            contentHolder.settings.seamlessDist = distTf.text.toInt()
            contentHolder.settings.seamlessAlpha = alphaCb.isSelected
            contentHolder.settings.seamlessOverlap = overlapCb.isSelected
        }
    }
}