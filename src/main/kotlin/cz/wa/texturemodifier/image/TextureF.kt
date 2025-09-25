package cz.wa.texturemodifier.image

import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.math.Vec2i
import java.awt.image.BufferedImage

/***
 * Creates new texture made of ColorF pixels from existing image.
 */
class TextureF(val width: Int, val height: Int) {
	val pixels: Array<Array<ColorF>>
	val size = Vec2i(width, height)

	init {
		pixels = Array(width) { Array(height) { ColorF.BLACK } }
	}

	constructor(tex: Texture) : this(tex.width, tex.height) {
		for (y in 0 until tex.height) {
			for (x in 0 until tex.width) {
				pixels[x][y] = ColorF(tex.getPoint(x, y))
			}
		}
	}

	constructor(tex: TextureF) : this(tex.width, tex.height) {
		for (y in 0 until tex.height) {
			for (x in 0 until tex.width) {
				pixels[x][y] = tex.pixels[x][y]
			}
		}
	}

	fun generateImage(): BufferedImage {
		val ret = ImageUtils.createEmptyImage(width, height)
		val tex = Texture(ret)
		for (y in 0 until height) {
			for (x in 0 until width) {
				tex.setPoint(x, y, pixels[x][y].rgb)
			}
		}
		return ret
	}
}
