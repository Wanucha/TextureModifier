package cz.wa.texturemodifier.gui

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ContentHolder(
    var settings: Settings,
    var files: MutableList<String>
) {

    var sourceImage: BufferedImage? = null
    set(value) {
        field = value
        outputImage = value
    }

    var outputImage: BufferedImage? = null

    val sourceFile
        get() =
            if (files.isEmpty()) {
                File(System.getProperty("user.dir"))
            } else {
                File(files[0])
            }


    private val settingsListeners = HashSet<(Settings) -> Unit>()

    fun addSettingsListener(l: (Settings) -> Unit) {
        settingsListeners.add(l)
    }

    fun removeSettingsListener(l: (Settings) -> Unit) {
        settingsListeners.remove(l)
    }

    fun callSettingsListeners() {
        for (l in settingsListeners) {
            l.invoke(settings)
        }
    }

    init {
        if (files.isNotEmpty()) {
            val file = File(files[0])
            try {
                sourceImage = ImageIO.read(file)
            } catch (e: Throwable) {
                println("Failed to read image ${file.absoluteFile}")
            }
        }
        if (sourceImage == null) {
            sourceImage = ImageUtils.createEmptyImage(1, 1)
        }
    }
}