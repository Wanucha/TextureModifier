package cz.wa.texturemodifier.gui.tabs.propertieseditor

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.utils.GuiUtils
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextArea

/**
 * Displays modification (command) of tile set
 */
class PropertiesEditor(val contentHolder: ContentHolder) : JPanel() {

    private val textArea = JTextArea()

    init {
        initComponents()
        val fol = object : MainFrame.FileOpenListener {
            override fun fileOpened(file: File) {
                reload(file)
            }
        }
        MainFrame.instance!!.addPropertiesOpenListener(fol)
    }

    private fun initComponents() {
        val toolPanel = JPanel()
        toolPanel.preferredSize = Dimension(90, 100)

        val applyB = JButton("Apply")
        applyB.addActionListener({ applySettings() })
        toolPanel.add(applyB)

        val generateB = JButton("Generate")
        generateB.addActionListener({ generateSettings() })
        toolPanel.add(generateB)

        val reloadB = JButton("Reload")
        reloadB.addActionListener({ reloadSettings() })
        toolPanel.add(reloadB)

        textArea.autoscrolls = true
        layout = BorderLayout()
        add(textArea, BorderLayout.CENTER)
        add(toolPanel, BorderLayout.EAST)
        if (contentHolder.settings.file != null) {
            reloadSettings()
        }
    }

    private fun applySettings() {
        if (textArea.text.isBlank()) {
            JOptionPane.showMessageDialog(this,"No properties defined")
        } else {
            contentHolder.settings = Settings.parseString(textArea.text)
        }
    }

    private fun generateSettings() {
        textArea.text = Settings.generateText(contentHolder.settings)
    }

    private fun reloadSettings() {
        if (contentHolder.settings.file == null) {
            JOptionPane.showMessageDialog(this,"No properties file opened")
        } else {
            reload(contentHolder.settings.file!!)
        }
    }

    fun reload(file: File) {
        GuiUtils.runCatch(this, Runnable {
            textArea.text = file.readText(Charsets.ISO_8859_1)
        })
    }
}
