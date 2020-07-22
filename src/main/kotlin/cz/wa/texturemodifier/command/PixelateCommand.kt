package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.ScaleType
import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.image.BufferedImage
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
        check(settings.pixelateScale >= 1) { throw IllegalArgumentException("pixelateScale must be >= 1") }
        check(settings.pixelateColors > 1 && settings.pixelateColors <= 256) { throw IllegalArgumentException("pixelateColors must be > 1 and <= 256") }
        check(settings.pixelateScaleColorTolerance >= 0 && settings.pixelateScaleColorTolerance < 256) {
            throw IllegalArgumentException(
                "pixelateScaleColorTolerance must be >= 0 and < 256"
            )
        }

        val inTex = Texture(image)

        val w2 = (image.width / settings.pixelateScale.toDouble()).roundToInt()
        val h2 = (image.height / settings.pixelateScale.toDouble()).roundToInt()
        val ret = ImageUtils.createEmptyImage(w2, h2)
        val outTex = Texture(ret)

        val r = image.width / w2.toDouble()

        for (y in 0 until h2) {
            for (x in 0 until w2) {
                val px = IntRange((r * x).roundToInt(), (r * (x + 1)).roundToInt() - 1)
                val py = IntRange((r * y).roundToInt(), (r * (y + 1)).roundToInt() - 1)
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
                    // TODO
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
        val counts = IntArray(colors * colors * colors)
        for (y in py) {
            for (x in px) {
                // TODO
            }
        }
        return 0
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
}