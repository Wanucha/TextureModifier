package cz.wa.texturemodifier.image

import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color

/**
 * Immutable color represented by 3 float values
 */
class ColorF(val r: Float, val g: Float, val b: Float) {
	constructor(r: Int, g: Int, b: Int) : this(r.toFloat(), g.toFloat(), b.toFloat())
	constructor(r: Double, g: Double, b: Double) : this(r.toFloat(), g.toFloat(), b.toFloat())
	constructor(rgb: Int) : this(rgb shr 16 and 0xFF, rgb shr 8 and 0xFF, rgb and 0xFF)

	val rgb = ColorUtils.fromRGB(r.toInt(), g.toInt(), b.toInt())

	/** Paints the color onto existing with alpha (0..1) */
	fun add(c: ColorF, a: Double): ColorF {
		return if (a >= 1) {
			c
		} else if (a <= 0) {
			this
		} else {
			lerp(c, a.toFloat())
		}
	}

	/** Linear interpolation without alpha ckeck */
	fun lerp(c: ColorF, a: Float): ColorF {
		val d = 1 - a
		return ColorF(r * d + c.r * a, g * d + c.g * a, b * d + c.b * a)
	}

	fun toColor() = Color(rgb)

	companion object {
		val FUCHSIA = ColorF(255, 0, 255)
		val WHITE = ColorF(255, 255, 255)
		val BLACK = ColorF(0, 0, 0)

		fun parse(s: String) =
			ColorF(Integer.parseInt(s.replaceFirst("#", ""), 16))
	}
}
