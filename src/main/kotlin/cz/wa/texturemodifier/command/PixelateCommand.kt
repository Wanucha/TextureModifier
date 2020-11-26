package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.ScaleType
import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Pixelates image.
 * scale - 4 means 32x32 -> 8x8
 * colors - how many colors per channel, 256 means no change
 * scale type - NEAREST: take one color in the rect, ignore others, MOST_COLOR - count colors in the rect and take the most
 * tolerance - when using MOST_COLOR, tolerance for the color, the result will be average of these colors
 * ignore bg color - when reducing pixels, will never choose bg color unless it fills whole rect
 * bg color - color when ignoring bg
 */
class PixelateCommand(settings: Settings) : AbstractCommand(settings) {


    override fun execute(image: BufferedImage): BufferedImage {
        check(settings.pixelateSizeX >= 1) { throw IllegalArgumentException("pixelateSizeX must be >= 1") }
        check(settings.pixelateSizeY >= 1) { throw IllegalArgumentException("pixelateSizeY must be >= 1") }
        check(settings.pixelateColors > 1 && settings.pixelateColors <= 256) { throw IllegalArgumentException("pixelateColors must be > 1 and <= 256") }
        check(settings.pixelateScaleColorTolerance >= 0 && settings.pixelateScaleColorTolerance < 256) {
            throw IllegalArgumentException(
                "pixelateScaleColorTolerance must be >= 0 and < 256"
            )
        }

        val inTex = Texture(image)

        val w2 =  settings.pixelateSizeX
        val h2 = settings.pixelateSizeY
        val ret = ImageUtils.createEmptyImage(w2, h2)
        val outTex = Texture(ret)

        val rx = image.width / w2.toDouble()
        val ry = image.height / h2.toDouble()

        for (y in 0 until h2) {
            for (x in 0 until w2) {
                val px = IntRange((rx * x).roundToInt(), (rx * (x + 1)).roundToInt() - 1)
                val py = IntRange((ry * y).roundToInt(), (ry * (y + 1)).roundToInt() - 1)
                processPixel(outTex, x, y, inTex, px, py)
            }
        }

        return ret
    }

    private fun processPixel(outTex: Texture, x: Int, y: Int, inTex: Texture, px: IntRange, py: IntRange) {
        val c: Int
        when (settings.pixelateScaleType) {
            ScaleType.NEAREST -> {
                if (!settings.pixelateIgnoreBgColor) {
                    c = roundColor(inTex.getPoint(middle(px), middle(py)))
                } else {
                    // TODO find first not BG
                    c = roundColor(inTex.getPoint(middle(px), middle(py)))
                }
            }
            ScaleType.MOST_COLOR -> {
                c = findMostColor(inTex, px, py)
            }
        }
        outTex.setPoint(x, y, c)
    }

    private fun findMostColor(inTex: Texture, px: IntRange, py: IntRange): Int {
        val colors = settings.pixelateColors
        val bgColor = settings.pixelateBgColor.rgb
        // count colors
        val counts = IntArray(colors * colors * colors)
        for (y in py) {
            for (x in px) {
                val c = inTex.getPoint(x, y)
                if (settings.pixelateIgnoreBgColor && c == bgColor) {
                    // ignore bg
                    continue
                }
                for (ci in getColorIndexRange(c)) {
                    counts[ci]++
                }
            }
        }
        // get the most
        var maxI = 0
        var maxC = 0
        for (i in 0 until counts.size) {
            val c = counts[i]
            if (c > maxC) {
                maxC = c
                maxI = i
            }
        }
        if (maxC == 0) {
            // only bg
            return settings.pixelateBgColor.rgb
        }

        return unindexColor(maxI)
    }

    private fun middle(r: IntRange) = (r.first + r.endInclusive) / 2

    private fun roundColor(c: Int): Int {
        val r = roundChannel(ColorUtils.getRed(c))
        val g = roundChannel(ColorUtils.getGreen(c))
        val b = roundChannel(ColorUtils.getBlue(c))
        return ColorUtils.fromRGB(r, g, b)
    }

    private fun roundChannel(a: Int) = if (settings.pixelateColors >= 256) {
        a
    } else {
        (a * (settings.pixelateColors / 256.0)).toInt() * 255 / (settings.pixelateColors - 1)
    }

    private fun getColorIndexRange(c: Int): List<Int> {
        val r = getColorIndexChannel(ColorUtils.getRed(c))
        val g = getColorIndexChannel(ColorUtils.getGreen(c))
        val b = getColorIndexChannel(ColorUtils.getBlue(c))
        val tol = getColorIndexChannel(settings.pixelateScaleColorTolerance)
        if (tol == 0) {
            return listOf(getColorIndex(r, g, b))
        }

        val ret = ArrayList<Int>()
        for (ri in getColorRange(r, tol)) {
            for (gi in getColorRange(g, tol)) {
                for (bi in getColorRange(b, tol)) {
                    ret.add(getColorIndex(ri, gi, bi))
                }
            }
        }
        return ret
    }

    private fun getColorRange(c: Int, tol: Int) : IntRange {
        return IntRange(max(c - tol, 0), min(c + tol, settings.pixelateColors - 1))
    }

    private fun unindexColor(c: Int): Int {
        val r = c % settings.pixelateColors
        val g = (c / settings.pixelateColors) % settings.pixelateColors
        val b = c / (settings.pixelateColors * settings.pixelateColors)
        val m = 255 / (settings.pixelateColors - 1)
        return ColorUtils.fromRGB(r * m, g * m, b * m)
    }

    private fun getColorIndex(c: Int): Int {
        val r = getColorIndexChannel(ColorUtils.getRed(c))
        val g = getColorIndexChannel(ColorUtils.getGreen(c))
        val b = getColorIndexChannel(ColorUtils.getBlue(c))
        return getColorIndex(r, g, b)
    }

    private fun getColorIndex(r: Int, g: Int, b: Int) =
        r + g * settings.pixelateColors + b * settings.pixelateColors * settings.pixelateColors

    private fun getColorIndexChannel(a: Int) = (a * (settings.pixelateColors / 256.0)).toInt()
}