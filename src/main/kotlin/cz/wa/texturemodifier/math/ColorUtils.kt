package cz.wa.texturemodifier.math

import java.awt.Color

object ColorUtils {
    fun lerp(c1: Int, c2: Int, a: Double): Int {
        val d = 1 - a
        val r = (c1 shr 16 and 0xFF) * d + (c2 shr 16 and 0xFF) * a
        val g = (c1 shr 8 and 0xFF) * d + (c2 shr 8 and 0xFF) * a
        val b = (c1 and 0xFF) * d + (c2 and 0xFF) * a
        return fromRGB(r, g, b)
    }

    fun fromRGB(r: Int, g: Int, b: Int): Int {
        return (0xFF shl 24) or
                (r and 0xFF shl 16) or
                (g and 0xFF shl 8) or
                (b and 0xFF shl 0)
    }

    fun fromRGB(r: Double, g: Double, b: Double) = fromRGB(r.toInt(), g.toInt(), b.toInt())

    fun parse(s: String): Color {
        return Color(Integer.parseInt(s.replaceFirst("#", ""), 16))
    }

    fun getRed(c: Int) = c shr 16 and 0xFF

    fun getGreen(c: Int) = c shr 8 and 0xFF

    fun getBlue(c: Int) = c and 0xFF

    fun getAlpha(c: Int) = c shr 24 and 0xFF
}