package cz.wa.texturemodifier.command

import java.awt.image.BufferedImage

interface Command {
	fun execute(image: BufferedImage): BufferedImage

	fun getHelp(): String
}
