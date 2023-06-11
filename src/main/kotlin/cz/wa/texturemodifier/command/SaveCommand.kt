package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.Settings
import java.awt.image.BufferedImage

/**
 * Saves output image to a file
 */
class SaveCommand(settings: Settings, val origName: String) : AbstractCommand(settings) {
    override fun execute(image: BufferedImage): BufferedImage {
        TODO("Not yet implemented")
    }

    override fun getHelp(): String {
        return "Saves the file, uses settings for final filename, takes care of overriding"
    }
}