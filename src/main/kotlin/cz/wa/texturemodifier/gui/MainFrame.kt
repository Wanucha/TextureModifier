package cz.wa.texturemodifier.gui

import cz.wa.texturemodifier.TextureModifierMain
import cz.wa.texturemodifier.gui.help.HelpFrame
import cz.wa.texturemodifier.gui.tabs.blur.BlurPanel
import cz.wa.texturemodifier.gui.tabs.fillbackground.FillBackgroundPanel
import cz.wa.texturemodifier.gui.tabs.mergemaps.MergeMapsPanel
import cz.wa.texturemodifier.gui.tabs.multiplycolor.MultiplyColorPanel
import cz.wa.texturemodifier.gui.tabs.pixelate.PixelatePanel
import cz.wa.texturemodifier.gui.tabs.removealpha.RemoveAlphaPanel
import cz.wa.texturemodifier.gui.tabs.seamless.SeamlessPanel
import cz.wa.texturemodifier.gui.tabs.settingseditor.SettingsEditor
import cz.wa.texturemodifier.gui.tabs.source.SourcePanel
import cz.wa.texturemodifier.gui.utils.ColorSlider
import cz.wa.texturemodifier.gui.utils.ConfirmFileChooser
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.settings.Settings
import cz.wa.texturemodifier.settings.io.SettingsIO
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class MainFrame(settings: Settings, settingsFile: File?, files: List<String>) : JFrame() {
    private val tabs: JTabbedPane = JTabbedPane()
    private val menu: JMenuBar = JMenuBar()
    private val help: HelpFrame = HelpFrame()
    private val quickOpenMenu = JPopupMenu()
    private val propsLabel = JMenuItem("= ")
    private val propsOpenChooser = JFileChooser()
    private val propsSaveChooser = ConfirmFileChooser()
    private val imageOpenChooser = JFileChooser()
    private val imageSaveChooser = ConfirmFileChooser()
    private val imagesFilter = FileNameExtensionFilter("Images (PNG, JPG, GIF, BMP)", *TextureModifierMain.IMAGE_OPEN_EXTS)

    private val imageOpenListeners = ArrayList<FileOpenListener>()
    private val imageRevertListeners = ArrayList<FileOpenListener>()
    private val settingsOpenListeners = ArrayList<FileOpenListener>()

    val contentHolder: ContentHolder
    val bgColorSlider = ColorSlider()

    init {
        instance = this
        val empty = ""
        title = "Texture modifier v${TextureModifierMain.VERSION}${empty}"
        defaultCloseOperation = EXIT_ON_CLOSE
        try {
            iconImages = loadIcons()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        contentHolder = if (files is MutableList) {
            ContentHolder(settings, settingsFile, files)
        } else {
            ContentHolder(settings, settingsFile, ArrayList(files))
        }

        // allow drop files
        transferHandler = FileTransferHandler()

        initComponents()
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val initW = 900
        val initH = 750
        bounds = Rectangle((screenSize.width - initW) / 2, (screenSize.height - initH) / 2, initW, initH)
        isVisible = true
    }

    private fun initComponents() {
        // Menu
        jMenuBar = menu

        // image
        val imageMenu = JMenu("Image")
        menu.add(imageMenu)

        val openedFile = if (contentHolder.files.isEmpty()) contentHolder.settingsFile
            ?: contentHolder.sourceFile else contentHolder.sourceFile

        val openImage = JMenuItem("Open")
        openImage.addActionListener { openImage() }
        openImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(openImage)
        imageOpenChooser.fileFilter = imagesFilter

        val quickOpenImage = JMenuItem("Open in directory")
        quickOpenImage.addActionListener { quickOpenImage() }
        quickOpenImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(quickOpenImage)

        val saveImage = JMenuItem("Save as")
        saveImage.addActionListener { saveImage() }
        saveImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(saveImage)
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Png", "png"))
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Gif", "gif"))
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Bmp", "bmp"))

        val reloadImage = JMenuItem("Reload")
        reloadImage.addActionListener { reloadImage() }
        reloadImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(reloadImage)

        val revertImage = JMenuItem("Revert")
        revertImage.addActionListener { revertImage() }
        revertImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(revertImage)

        // settings
        val propMenu = JMenu("Settings")
        menu.add(propMenu)

        val openProp = JMenuItem("Open")
        openProp.addActionListener { openSettings() }
        openProp.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK)
        propMenu.add(openProp)
        propsOpenChooser.fileFilter = FileNameExtensionFilter("Settings (.yml, .yaml, .properties)", "yml", "yaml", "properties")

        val saveProp = JMenuItem("Save as")
        saveProp.addActionListener { saveSettings() }
        saveProp.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK)
        propMenu.add(saveProp)
        propsSaveChooser.fileFilter = FileNameExtensionFilter("Settings (.yml, .yaml)", "yml", "yaml")

        // props label
        propsLabel.isEnabled = false
        propsLabel.text = "= ${contentHolder.settingsFile?.name}"
        menu.add(propsLabel)

        // args help
        val argsHelp = JMenuItem("Program args")
        argsHelp.addActionListener { showArgsHelp() }
        menu.add(argsHelp)

        // help
        val help = JMenuItem("Help")
        help.addActionListener { showHelp() }
        menu.add(help)

        // show bounds
        val boundsCb = JCheckBox("Show bounds")
        boundsCb.isSelected = true
        boundsCb.addActionListener { contentHolder.settings.gui.showBounds = boundsCb.isSelected }
        menu.add(boundsCb)

        // bg color
        bgColorSlider.addListener { contentHolder.settings.gui.backgroundColor = Color(it, it, it) }
        bgColorSlider.toolTipText = "Change background color"
        menu.add(bgColorSlider)

        // Tabs
        this@MainFrame.layout = BorderLayout()
        add(tabs, BorderLayout.CENTER)

        tabs.addTab("Source image", SourcePanel(contentHolder))
        tabs.addTab("Seamless", SeamlessPanel(contentHolder))
        tabs.addTab("Blur", BlurPanel(contentHolder))
        tabs.addTab("Pixelate", PixelatePanel(contentHolder))
        tabs.addTab("Fill background", FillBackgroundPanel(contentHolder))
        tabs.addTab("Merge maps", MergeMapsPanel(contentHolder))
        tabs.addTab("Multiply color", MultiplyColorPanel(contentHolder))
        tabs.addTab("Remove alpha", RemoveAlphaPanel(contentHolder))
        tabs.addTab("Settings", SettingsEditor(contentHolder))

        // select each tab
        tabs.selectedIndex = tabs.tabCount - 1
        showPrevTab()
        SwingUtilities.invokeLater { initComponentsLater(openedFile) }
    }

    private fun initComponentsLater(imageFile: File) {
        imageOpenChooser.currentDirectory = imageFile
        imageSaveChooser.currentDirectory = imageFile
        propsOpenChooser.currentDirectory = contentHolder.settingsFile ?: contentHolder.sourceFile
        propsSaveChooser.currentDirectory = contentHolder.settingsFile ?: contentHolder.sourceFile
    }

    private fun loadIcons(): List<BufferedImage> {
        return listOf(
            ImageIO.read(MainFrame::class.java.getResourceAsStream("/icon16.png")),
            ImageIO.read(MainFrame::class.java.getResourceAsStream("/icon32.png")),
            ImageIO.read(MainFrame::class.java.getResourceAsStream("/icon64.png"))
        )
    }

    private fun showPrevTab() {
        if (tabs.selectedIndex == 0) {
            return
        }
        SwingUtilities.invokeLater {
            tabs.selectedIndex--
            showPrevTab()
        }
    }

    fun addImageOpenListener(l: FileOpenListener) {
        imageOpenListeners.add(l)
    }

    fun addImageRevertListener(l: FileOpenListener) {
        imageRevertListeners.add(l)
    }

    fun addSettingsOpenListener(l: FileOpenListener) {
        settingsOpenListeners.add(l)
    }

    private fun openImage() {
        if (imageOpenChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            GuiUtils.runCatch(this) {
                openImage(imageOpenChooser.selectedFile)
            }
        }
    }

    private fun openImage(file: File) {
        contentHolder.sourceImage = ImageIO.read(file)
        if (contentHolder.files.isEmpty()) {
            contentHolder.files.add("")
        }
        contentHolder.files[0] = file.absolutePath
        imageSaveChooser.selectedFile = replaceJpgByPng(file)
        tabs.selectedComponent.paint(tabs.selectedComponent.graphics)
        SwingUtilities.invokeLater {
            for (l in imageOpenListeners) {
                l.fileOpened(file)
            }
        }
    }

    private fun replaceJpgByPng(file: File): File {
        if (TextureModifierMain.IMAGE_JPG_EXTS.contains(file.extension.lowercase())) {
            return File(file.path.substringBeforeLast('.') + ".png")
        }
        return file
    }

    private fun quickOpenImage() {
        quickOpenMenu.removeAll()
        val files =
            contentHolder.sourceFile.parentFile.listFiles { f -> TextureModifierMain.IMAGE_OPEN_EXTS.any { f.extension.lowercase() == it } }
        for (file in files!!) {
            val item = JMenuItem(file.name)
            item.addActionListener {
                openImage(file)
            }
            quickOpenMenu.add(item)
        }
        val mPos = mousePosition
        quickOpenMenu.show(this, mPos.x, mPos.y)
    }

    private fun saveImage() {
        if (contentHolder.outputImage != null) {
            if (imageSaveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                var file = imageSaveChooser.selectedFile
                if (file.extension.isBlank()) {
                    file = File(file.path + '.' + contentHolder.sourceFile.extension)
                }

                if (!TextureModifierMain.IMAGE_SAVE_EXTS.contains(file.extension.lowercase())) {
                    JOptionPane.showMessageDialog(this, "Unknown image extension: '${file.extension}'\n" +
                            "Supported save formats: ${TextureModifierMain.IMAGE_SAVE_EXTS.joinToString(", ")}")
                    return
                }
                GuiUtils.runCatch(this) {
                    ImageIO.write(contentHolder.outputImage, file.extension, file)
                    if (!file.isFile) {
                        JOptionPane.showMessageDialog(this@MainFrame, "File not saved: ${file.absolutePath}")
                    }
                }
            }
        }
    }

    private fun reloadImage() {
        openImage(File(contentHolder.files[0]))
    }

    private fun revertImage() {
        contentHolder.outputImage = contentHolder.sourceImage
        imageRevertListeners.forEach { it.fileOpened(File(contentHolder.files[0])) }
    }

    private fun openSettings() {
        if (propsOpenChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            GuiUtils.runCatch(this) {
                val file = propsOpenChooser.selectedFile
                contentHolder.settings = SettingsIO.load(file)
                propsLabel.text = "= ${contentHolder.settingsFile?.name}"
                SwingUtilities.invokeLater {
                    for (l in settingsOpenListeners) {
                        l.fileOpened(file)
                    }
                    contentHolder.callSettingsListeners()
                }
            }
        }
    }

    private fun saveSettings() {
        if (propsSaveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = propsSaveChooser.selectedFile
            if (file.extension.isBlank()) {
                file = File(file.path + ".yml")
            }
            GuiUtils.runCatch(this) {
                SettingsIO.save(file, contentHolder.settings)
                if (!file.isFile) {
                    JOptionPane.showMessageDialog(this@MainFrame, "File not saved: ${file.absolutePath}")
                }
            }
        }
    }

    private fun showArgsHelp() {
        JOptionPane.showMessageDialog(
            this, "${TextureModifierMain.printTitle()}${TextureModifierMain.printUsage()}"
        )
    }

    private fun showHelp() {
        help.isVisible = true
    }

    interface FileOpenListener {
        fun fileOpened(file: File)
    }

    private class FileTransferHandler : TransferHandler() {
        override fun canImport(support: TransferSupport): Boolean {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
        }

        override fun importData(support: TransferSupport): Boolean {
            if (!canImport(support)) {
                return false
            }
            val t = support.transferable
            val files =
                t.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
            for (file in files) {
                if (TextureModifierMain.IMAGE_OPEN_EXTS.contains(file.extension.lowercase())) {
                    GuiUtils.runCatch(instance!!) {
                        instance!!.openImage(file)
                    }
                    return true
                }
            }
            return false
        }
    }

    companion object {
        var instance: MainFrame? = null
    }
}
