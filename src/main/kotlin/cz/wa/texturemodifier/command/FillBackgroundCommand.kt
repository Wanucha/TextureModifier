package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import cz.wa.texturemodifier.math.Vec2i
import cz.wa.texturemodifier.settings.Settings
import java.awt.image.BufferedImage

/**
 * Fills background with color computed from neighbouring pixels.
 */
class FillBackgroundCommand(settings: Settings) : AbstractCommand(settings) {

	private var bgColor: Int = 0

	override fun execute(image: BufferedImage): BufferedImage {
		check(settings.fillBackground.iterations >= 1) { "fillBgIterations must be >= 1" }

		var inTex = Texture(image)
		val ret = ImageUtils.copyImage(image)
		var outTex = Texture(ret)
		bgColor = settings.fillBackground.bgColor.rgb

		var started = false
		for (i in 0 until settings.fillBackground.iterations) {
			if (started) {
				inTex = outTex
				outTex = Texture(ImageUtils.copyImage(inTex.img))
			}
			for (y in 0 until image.height) {
				for (x in 0 until image.width) {
					processPixel(inTex, x, y, outTex, bgColor)
				}
			}
			started = true
		}

		return outTex.img
	}

	override fun getHelp(): String = "Fills pixels of background color with color computed from neighbors\n" +
			"For each BG pixel computes new color from all non BG colors\n" +
			"* iterations - apply modifier multiple times\n" +
			"* include corner pixels - false uses only 4 adject pixels, true uses 8 pixels around\n" +
			"* average color - true averages all neighboring colors, false takes the most frequent color\n" +
			"* BG color - define which color will be replaced"

	private fun processPixel(inTex: Texture, x: Int, y: Int, outTex: Texture, bgColor: Int) {
		if (inTex.getPoint(x, y) == bgColor && hasNearColor(inTex, x, y)) {
			outTex.setPoint(x, y, computeColor(inTex, x, y))
		}
	}

	private fun hasNearColor(inTex: Texture, x: Int, y: Int): Boolean {
		var ret = false
		iterateNearPixels(inTex, x, y) {
			if (it != bgColor) {
				ret = true
				false
			} else {
				true
			}
		}
		return ret
	}

	private fun computeColor(inTex: Texture, x: Int, y: Int): Int {
		val colors = ArrayList<Int>(if (settings.fillBackground.includeCorners) 8 else 4)
		iterateNearPixels(inTex, x, y) {
			if (it != bgColor) {
				colors.add(it)
			}
			true
		}

		return if (settings.fillBackground.averageFill) {
			ColorUtils.averageColor(colors)
		} else {
			mostColor(colors)
		}
	}

	private fun mostColor(colors: ArrayList<Int>): Int {
		val counts = HashMap<Int, Int>(colors.size)
		for (c in colors) {
			if (counts.containsKey(c)) {
				counts[c] = counts.getValue(c) + 1
			} else {
				counts[c] = 1
			}
		}
		var max = 0
		var ret = 0
		for (entry in counts) {
			if (entry.value > max) {
				max = entry.value
				ret = entry.key
			}
		}
		return ret
	}

	private fun iterateNearPixels(inTex: Texture, x: Int, y: Int, function: (Int) -> Boolean) {
		for (p in NEIGHBORS1) {
			if (inTex.containsPoint(x + p.x, y + p.y) && !function.invoke(inTex.getPoint(x + p.x, y + p.y))) {
				return
			}
		}
		if (settings.fillBackground.includeCorners) {
			for (p in NEIGHBORS2) {
				if (inTex.containsPoint(x + p.x, y + p.y) && !function.invoke(inTex.getPoint(x + p.x, y + p.y))) {
					return
				}
			}
		}
	}

	companion object {
		private val NEIGHBORS1 = arrayOf(
			Vec2i(-1, 0),
			Vec2i(0, -1),
			Vec2i(1, 0),
			Vec2i(0, 1)
		)
		private val NEIGHBORS2 = arrayOf(
			Vec2i(-1, -1),
			Vec2i(1, -1),
			Vec2i(1, 1),
			Vec2i(-1, 1)
		)
	}
}
