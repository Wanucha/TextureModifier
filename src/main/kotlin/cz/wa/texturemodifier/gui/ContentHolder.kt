package cz.wa.texturemodifier.gui

import cz.wa.tilepalleteresize.Settings
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ContentHolder(var settings: Settings, var files: MutableList<String>) {

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


    init {
        if (files.isNotEmpty()) {
            val file = File(files[0])
            try {
                sourceImage = ImageIO.read(file)
            } catch (e: Throwable) {
                println("Failed to read image ${file.absoluteFile}")
            }
        }
    }
}