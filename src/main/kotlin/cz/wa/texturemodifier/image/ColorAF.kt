package cz.wa.texturemodifier.image

import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color

/**
 * Immutable color with alpha represented by 4 float values
 */
class ColorAF(val r: Float, val g: Float, val b: Float, val a: Float) {
    constructor(r: Int, g: Int, b: Int, a: Int): this(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
    constructor(r: Double, g: Double, b: Double, a: Double): this(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
    constructor(rgba: Int): this(rgba shr 16 and 0xFF, rgba shr 8 and 0xFF, rgba and 0xFF, rgba shr 24 and 0xFF)

    val rgba = ColorUtils.fromRGBA(r.toInt(), g.toInt(), b.toInt(), a.toInt())

    /** Paints the color onto existing with alpha (0..1) */
    fun add(c: ColorAF, a: Double): ColorAF {
        return if (a >= 1) {
            c
        } else if (a <= 0) {
            this
        } else {
            lerp(c, a.toFloat())
        }
    }

    /** Linear interpolation without alpha check */
    fun lerp(c: ColorAF, x: Float): ColorAF {
        val d = 1 - x
        return ColorAF(r * d + c.r * x, g * d + c.g * x, b * d + c.b * x, a * d + c.a * x)
    }

    fun toColor() = Color(rgba)

    companion object {
        fun parse(s: String) =
            ColorAF(Integer.parseInt(s.replaceFirst("#", ""), 16))
    }
}
