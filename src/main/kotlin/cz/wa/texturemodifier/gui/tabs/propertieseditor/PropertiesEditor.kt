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

        val helpB = JButton("Help")
        helpB.addActionListener { showHelp() }
        toolPanel.add(helpB)

        val applyB = JButton("Apply")
        applyB.addActionListener { applySettings() }
        applyB.toolTipText = "Apply lines from this editor to current settings"
        toolPanel.add(applyB)

        val generateB = JButton("Generate")
        generateB.addActionListener { generateSettings() }
        generateB.toolTipText = "From current settings generate text properties"
        toolPanel.add(generateB)

        val reloadB = JButton("Reload")
        reloadB.addActionListener { reloadSettings() }
        reloadB.toolTipText = "Reload properties from file if loaded"
        toolPanel.add(reloadB)

        textArea.autoscrolls = true
        layout = BorderLayout()
        add(textArea, BorderLayout.CENTER)
        add(toolPanel, BorderLayout.EAST)
        if (contentHolder.settings.file != null) {
            reloadSettings()
        }
    }

    private fun showHelp() {
        JOptionPane.showMessageDialog(this,"This editor can generate and save properties, usage:\n" +
                "By default it considers startup-loaded properties or default values\n\n" +
                "How to save current properties:\n" +
                "1. Modify settings in a tab\n" +
                "2. Click apply in the modified - now also the properties are updated\n" +
                "3. Generate properties\n" +
                "4. Save the properties\n\n" +
                "If you want the properties load the next time you start the app, provide path to the properties as a program argument")
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
        GuiUtils.runCatch(this) {
            textArea.text = file.readText(Charsets.ISO_8859_1)
        }
    }
}
