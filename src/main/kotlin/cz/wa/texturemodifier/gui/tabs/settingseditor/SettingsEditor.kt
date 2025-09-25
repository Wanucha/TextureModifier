package cz.wa.texturemodifier.gui.tabs.settingseditor

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.settings.io.SettingsIO
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities

/**
 * Displays modification (command) of tile set
 */
class SettingsEditor(val contentHolder: ContentHolder) : JPanel() {

	private val textArea = JTextArea()

	init {
		initComponents()
		val fol = object : MainFrame.FileOpenListener {
			override fun fileOpened(file: File) {
				reload(file)
			}
		}
		MainFrame.instance!!.addSettingsOpenListener(fol)
	}

	private fun initComponents() {
		val toolPanel = JPanel()
		toolPanel.preferredSize = Dimension(90, 100)

		val helpB = JButton("Help")
		helpB.addActionListener { showHelp() }
		toolPanel.add(helpB)

		val applyB = JButton("Apply")
		applyB.addActionListener { applySettings() }
		applyB.toolTipText = "Apply text from this editor to current settings"
		toolPanel.add(applyB)

		val generateB = JButton("Generate")
		generateB.addActionListener { generateSettings() }
		generateB.toolTipText = "From current settings generate text to this editor"
		toolPanel.add(generateB)

		val reloadB = JButton("Reload")
		reloadB.addActionListener { reloadSettings() }
		reloadB.toolTipText = "Reload settings from file if previously loaded"
		toolPanel.add(reloadB)

		val scrollPane = JScrollPane(textArea)
		scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
		scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED

		textArea.autoscrolls = true
		this@SettingsEditor.layout = BorderLayout()
		add(scrollPane, BorderLayout.CENTER)
		add(toolPanel, BorderLayout.EAST)
		if (contentHolder.settingsFile != null) {
			reloadSettings()
		}
	}

	private fun showHelp() {
		JOptionPane.showMessageDialog(
			this, "This editor can generate and save settings as yml, usage:\n" +
					"By default it considers startup-loaded settings or default values\n\n" +
					"How to save current settings:\n" +
					"1. Modify settings in a tab\n" +
					"2. Click apply in the modified - now also the settings are updated\n" +
					"3. Generate settings\n" +
					"\t- You can modify the settings\n" +
					"\t- Click apply to save the change\n" +
					"4. Save the settings\n\n" +
					"If you want the settings load the next time you start the app, provide path to the settings as a program argument"
		)
	}

	private fun applySettings() {
		if (textArea.text.isBlank()) {
			JOptionPane.showMessageDialog(this, "No settings defined")
		} else {
			contentHolder.settings = SettingsIO.loadFromString(textArea.text)
			SwingUtilities.invokeLater {
				contentHolder.callSettingsListeners()
			}
		}
	}

	private fun generateSettings() {
		textArea.text = SettingsIO.saveToString(contentHolder.settings)
	}

	private fun reloadSettings() {
		if (contentHolder.settingsFile == null) {
			JOptionPane.showMessageDialog(this, "No settings file opened")
		} else {
			reload(contentHolder.settingsFile!!)
		}
	}

	fun reload(file: File) {
		GuiUtils.runCatch(this) {
			val settings = SettingsIO.load(file)
			textArea.text = SettingsIO.saveToString(settings)
		}
	}
}
