package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.OverwriteType
import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.TextureModifierMain
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Saves output image to a file.
 * Generates new name and saves the file using settings. If no format specified, uses original format.
 */
class SaveCommand(settings: Settings, private val stats: SaveStats, private val origFile: File) : AbstractCommand(settings) {
    override fun execute(image: BufferedImage): BufferedImage {
        var newName = getFileName()
        var newFile = File(newName)

        if (!TextureModifierMain.IMAGE_SAVE_EXTS.contains(newFile.extension.lowercase())) {
            println("Unknown image extension: '${newFile.extension}', " +
                    "Supported save formats: ${TextureModifierMain.IMAGE_SAVE_EXTS.joinToString(", ")}")
            stats.errors++
            return image;
        }

        // check overwrite
        var existed = false
        if (newFile.isFile) {
            if (settings.overwriteType == OverwriteType.OVERWRITE) {
                existed = true
                println("Overwriting file: $newName")
            } else {
                println("File exists, skipped: $newName")
                stats.skipped++
                return image
            }
        } else {
            println("Saving to: $newName")
        }

        // ensure directory
        val directory = newFile.parentFile
        if (!directory.isDirectory) {
            directory.mkdirs()
        }

        // save
        ImageIO.write(image, newFile.extension, newFile)
        if (!newFile.isFile) {
            println("Error, failed to save: $newName")
        } else {
            stats.savedTotal++
            if (existed) {
                stats.overwritten++
            }
        }
        return image
    }

    private fun getFileName(): String {
        val dir = origFile.parentFile
        val fileName = origFile.nameWithoutExtension
        var ext = if (settings.outFormat.isNullOrBlank()) origFile.extension else settings.outFormat
        if (ext.lowercase() in TextureModifierMain.IMAGE_JPG_EXTS) {
           ext = "png"
        }

        return "${dir.path}/${settings.outPrefix}${fileName}${settings.outPostfix}.${ext}"
    }

    override fun getHelp(): String {
        return "Saves the file, uses settings for final filename, takes care of overriding"
    }
}