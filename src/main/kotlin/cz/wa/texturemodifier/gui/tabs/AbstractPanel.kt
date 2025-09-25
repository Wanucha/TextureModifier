package cz.wa.texturemodifier.gui.tabs

import cz.wa.texturemodifier.command.Command
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel

abstract class AbstractPanel<V : TextureViewer>(val contentHolder: ContentHolder, val canvas: V) : JPanel() {
	protected val toolPanel: AbstractToolPanel<V>

	init {
		toolPanel = createPanel(contentHolder, canvas)
		initComponents()
	}

	protected open fun initComponents() {
		this@AbstractPanel.layout = BorderLayout()
		MainFrame.instance!!.bgColorSlider.addListener { canvas.refresh() }
		add(canvas, BorderLayout.CENTER)
		add(toolPanel, BorderLayout.EAST)
	}

	protected abstract fun createPanel(contentHolder: ContentHolder, canvas: V): AbstractToolPanel<V>

	protected abstract class AbstractToolPanel<V : TextureViewer>(
		val contentHolder: ContentHolder,
		val canvas: V,
		width: Int,
		val command: Class<out Command>
	) : JPanel() {
		init {
			maximumSize = Dimension(width, 4096)
			preferredSize = maximumSize
			contentHolder.addSettingsListener { showSettings() }
		}

		protected open fun apply() {
			applySettings()
			contentHolder.outputImage = createCommand().execute(contentHolder.getSourceImageIntBuffer())
			canvas.refresh()
		}

		protected abstract fun showSettings()

		protected abstract fun applySettings()

		protected open fun createCommand() = command.constructors.first().newInstance(contentHolder.settings) as Command

		protected open fun getHelp(): String {
			try {
				return createCommand().getHelp()
			} catch (e: Exception) {
				e.printStackTrace()
				return e.message.orEmpty()
			}
		}

		protected fun createApplyButton(): JButton {
			val ret = JButton("Apply")
			ret.addActionListener {
				GuiUtils.runCatch(this) {
					apply()
				}
			}
			return ret
		}

		protected fun createHelpButton(): JButton {
			val ret = JButton("Help")
			ret.addActionListener {
				GuiUtils.runCatch(this) {
					JOptionPane.showMessageDialog(
						this, getHelp()
					)
				}
			}
			return ret
		}

		protected fun createEmptyPanel(width: Int): JPanel {
			val ret = JPanel()
			ret.preferredSize = Dimension(width, 1)
			ret.minimumSize = ret.preferredSize
			return ret
		}
	}
}
