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
        var sizeX = settings.pixelateSizeX
        var sizeY = settings.pixelateSizeY
        if (!settings.pixelateUseSize) {
            sizeX = (image.width / settings.pixelateScale).roundToInt()
            sizeY = (image.height / settings.pixelateScale).roundToInt()
        }

        check(sizeX >= 1) { "pixelateSizeX must be >= 1" }
        check(sizeY >= 1) { "pixelateSizeY must be >= 1" }
        check(settings.pixelateColors in 2..256) { "pixelateColors must be > 1 and <= 256" }
        check(settings.pixelateScaleColorTolerance in 0..255) {
            "pixelateScaleColorTolerance must be >= 0 and < 256"
        }
        check(settings.pixelateBlendSmooth in 0.0..1.0) {"pixelateBlendSmooth must be 0..1"}

        val inTex = Texture(image)

        val w2 = sizeX
        val h2 = sizeY
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

    override fun getHelp(): String = "Reduce resolution and number of colors\n" +
            "* scale down - only modifies final resolution when used\n" +
            "* size XY - final resolution\n" +
            "* colors per channel - color reduction, 10 means each channel can have only 10 different values (spaced equally)\n" +
            "* filter type - scaling pixel filter\n" +
            "\t- NEAREST - takes nearest pixel\n" +
            "\t- MOST_COLOR - take colors from all pixels, that the current one is generated from and use the most frequent color\n" +
            "* scale color tolerance - when using MOST_COLOR, colors within this range will be considered same, the result is average from these\n" +
            "* ignore BG color - when finding new color, this color will be ignored by all filters\n" +
            "* BG color - specify the ignored color\n" +
            "* Smooth blend ratio - 0 means pixelated, 1 means smooth, 0.5 means blend between pixelated and smooth\n" +
            "* Smooth filter type:\n" +
            "\t - BILINEAR - only bilinear filter\n" +
            "\t - ANISO - before filtering, scales down the map and averages color\n" +
            "\t - ANISO_BILINEAR - combines both (smoothest)"

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

    private fun getColorRange(c: Int, tol: Int): IntRange {
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