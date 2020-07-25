package cz.wa.texturemodifier.gui

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.TextureModifierMain
import cz.wa.texturemodifier.gui.tabs.blur.BlurPanel
import cz.wa.texturemodifier.gui.tabs.blur.PixelatePanel
import cz.wa.texturemodifier.gui.tabs.propertieseditor.PropertiesEditor
import cz.wa.texturemodifier.gui.tabs.seamless.SeamlessPanel
import cz.wa.texturemodifier.gui.tabs.source.SourcePanel
import cz.wa.texturemodifier.gui.utils.ColorSlider
import cz.wa.texturemodifier.gui.utils.ConfirmFileChooser
import cz.wa.texturemodifier.gui.utils.GuiUtils
import java.awt.BorderLayout
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.KeyEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class MainFrame(settings: Settings, files: List<String>) : JFrame() {
    private val tabs: JTabbedPane = JTabbedPane()
    private val menu: JMenuBar = JMenuBar()
    private val quickOpenMenu = JPopupMenu()
    private val propsLabel = JMenuItem("= ")
    private val propsOpenChooser = JFileChooser()
    private val propsSaveChooser = ConfirmFileChooser()
    private val imageOpenChooser = JFileChooser()
    private val imageSaveChooser = ConfirmFileChooser()
    private val imagesFilter = FileNameExtensionFilter("Images (PNG, JPG, GIF, BMP)", *IMAGE_EXTS)

    private val imageOpenListeners = ArrayList<FileOpenListener>()
    private val propertiesOpenListeners = ArrayList<FileOpenListener>()

    val contentHolder: ContentHolder

    init {
        instance = this
        title = "Texture modifier v${TextureModifierMain.VERSION}"
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        if (files is MutableList) {
            contentHolder = ContentHolder(settings, files)
        } else {
            contentHolder = ContentHolder(settings, ArrayList<String>(files))
        }

        // allow drop files
        transferHandler = FileTransferHandler()

        initComponents()
        val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
        val initW = 800
        val initH = 500
        bounds = Rectangle((screenSize.width - initW) / 2, (screenSize.height - initH) / 2, initW, initH)
        isVisible = true
    }

    private fun initComponents() {
        // Menu
        jMenuBar = menu

        // image
        val imageMenu = JMenu("Image")
        menu.add(imageMenu)

        val imageFile = if (contentHolder.files.isEmpty()) contentHolder.settings.file
            ?: contentHolder.sourceFile else contentHolder.sourceFile

        val openImage = JMenuItem("Open")
        openImage.addActionListener({ openImage() })
        openImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(openImage)
        imageOpenChooser.fileFilter = imagesFilter;
        imageOpenChooser.currentDirectory = imageFile

        val quickOpenImage = JMenuItem("Open in directory")
        quickOpenImage.addActionListener({ quickOpenImage() })
        quickOpenImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(quickOpenImage)

        val saveImage = JMenuItem("Save as")
        saveImage.addActionListener({ saveImage() })
        saveImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(saveImage)
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Png", "png"))
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Jpg", "jpg"))
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Jpeg", "jpeg"))
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Gif", "gif"))
        imageSaveChooser.addChoosableFileFilter(FileNameExtensionFilter("Bmp", "bmp"))
        imageSaveChooser.currentDirectory = imageFile

        val reloadImage = JMenuItem("Reload")
        reloadImage.addActionListener({ reloadImage() })
        reloadImage.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)
        imageMenu.add(reloadImage)

        // properties
        val propMenu = JMenu("Properties");
        menu.add(propMenu);

        val openProp = JMenuItem("Open")
        openProp.addActionListener({ openProperties() })
        openProp.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK)
        propMenu.add(openProp)
        propsOpenChooser.fileFilter = FileNameExtensionFilter("Properties", "properties");
        propsOpenChooser.currentDirectory = contentHolder.settings.file ?: contentHolder.sourceFile

        val saveProp = JMenuItem("Save as")
        saveProp.addActionListener({ saveProperties() })
        propMenu.add(saveProp)
        propsSaveChooser.fileFilter = FileNameExtensionFilter("Properties", "properties");
        propsSaveChooser.currentDirectory = contentHolder.settings.file ?: contentHolder.sourceFile

        // props label
        propsLabel.isEnabled = false
        propsLabel.text = "= ${contentHolder.settings.file?.name}"
        menu.add(propsLabel)

        // help
        val help = JMenuItem("Program args help")
        help.addActionListener({ showHelp() })
        menu.add(help)

        // bg color
        val bgColor = ColorSlider(contentHolder)
        bgColor.toolTipText = "Change background color"
        menu.add(bgColor)

        // Tabs
        layout = BorderLayout()
        add(tabs, BorderLayout.CENTER)

        tabs.addTab("Source image", SourcePanel(contentHolder))
        tabs.addTab("Seamless", SeamlessPanel(contentHolder))
        tabs.addTab("Blur", BlurPanel(contentHolder))
        tabs.addTab("Pixelate", PixelatePanel(contentHolder))
        tabs.addTab("Properties", PropertiesEditor(contentHolder))

        // select each tab
        tabs.selectedIndex = tabs.tabCount - 1
        showPrevTab()
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

    fun addPropertiesOpenListener(l: FileOpenListener) {
        propertiesOpenListeners.add(l)
    }

    public fun openImage() {
        if (imageOpenChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            GuiUtils.runCatch(this, Runnable {
                openImage(imageOpenChooser.selectedFile)
            })
        }
    }

    private fun openImage(file: File) {
        contentHolder.sourceImage = ImageIO.read(file)
        if (contentHolder.files.isEmpty()) {
            contentHolder.files.add("");
        }
        contentHolder.files[0] = file.absolutePath
        imageSaveChooser.selectedFile = file
        tabs.selectedComponent.paint(tabs.selectedComponent.graphics)
        SwingUtilities.invokeLater(Runnable {
            for (l in imageOpenListeners) {
                l.fileOpened(file)
            }
        })
    }

    public fun quickOpenImage() {
        quickOpenMenu.removeAll()
        val files =
            contentHolder.sourceFile.parentFile.listFiles { f -> IMAGE_EXTS.any { f.name.endsWith(".${it}") } }
        for (file in files!!) {
            val item = JMenuItem(file.name)
            item.addActionListener({
                openImage(file)
            })
            quickOpenMenu.add(item)
        }
        val mPos = mousePosition
        quickOpenMenu.show(this, mPos.x, mPos.y)
    }

    public fun saveImage() {
        if (contentHolder.outputImage != null) {
            if (imageSaveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                val file = imageSaveChooser.selectedFile
                if (!IMAGE_EXTS.contains(file.extension)) {
                    JOptionPane.showMessageDialog(this, "Unknown image extension: ${file.extension}")
                    return
                }
                GuiUtils.runCatch(this, Runnable {
                    ImageIO.write(contentHolder.outputImage, file.extension, file)
                    if (!file.isFile) {
                        JOptionPane.showMessageDialog(this@MainFrame, "File not saved: ${file.absolutePath}")
                    }
                })
            }
        }
    }

    public fun reloadImage() {
        openImage(File(contentHolder.files[0]))
    }

    public fun openProperties() {
        if (propsOpenChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            GuiUtils.runCatch(this, Runnable {
                val file = propsOpenChooser.selectedFile
                contentHolder.settings = Settings.parseFile(file.absolutePath)
                propsLabel.text = "= ${contentHolder.settings.file?.name}"
                SwingUtilities.invokeLater(Runnable {
                    for (l in propertiesOpenListeners) {
                        l.fileOpened(file)
                    }
                })
            })
        }
    }

    public fun saveProperties() {
        if (propsSaveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            val file = propsSaveChooser.selectedFile
            GuiUtils.runCatch(this, Runnable {
                Settings.save(contentHolder.settings, file);
                if (!file.isFile) {
                    JOptionPane.showMessageDialog(this@MainFrame, "File not saved: ${file.absolutePath}")
                }
            })
        }
    }

    private fun showHelp() {
        JOptionPane.showMessageDialog(
            this, "${TextureModifierMain.printTitle()}${TextureModifierMain.printUsage()}"
        )
    }

    interface FileOpenListener {
        fun fileOpened(file: File)
    }

    private class FileTransferHandler : TransferHandler() {
        override fun canImport(support: TransferHandler.TransferSupport): Boolean {
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false
            }
            return true
        }

        override fun importData(support: TransferSupport): Boolean {
            if (!canImport(support)) {
                return false
            }
            val t = support.transferable
            val files =
                t.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
            for (file in files) {
                if (IMAGE_EXTS.contains(file.extension)) {
                    GuiUtils.runCatch(MainFrame.instance!!, Runnable {
                        MainFrame.instance!!.openImage(file)
                    })
                    return true
                }
            }
            return false
        }
    }

    companion object {
        public val IMAGE_EXTS = arrayOf("png", "jpg", "jpeg", "gif", "bmp")

        public var instance: MainFrame? = null
    }
}
