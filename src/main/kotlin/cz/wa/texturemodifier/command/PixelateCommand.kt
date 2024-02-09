package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.ScaleType
import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.SmoothType
import cz.wa.texturemodifier.gui.utils.FilterType
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.ColorAF
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import cz.wa.texturemodifier.math.Vec2f
import cz.wa.texturemodifier.math.Vec2i
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

        validateSettings(image, sizeX, sizeY)

        var img = image
        if (settings.pixelateMiddleUse) {
            img = doMiddleStep(img, sizeX, sizeY)
        }

        return pixelateImage(img, sizeX, sizeY)
    }

    private fun pixelateImage(image: BufferedImage, sizeX: Int, sizeY: Int): BufferedImage {
        if (image.width == sizeX && image.height == sizeY) {
            // same size, just copy image
            return ImageUtils.copyImage(image)
        }

        val inTex = Texture(image)

        val ret: BufferedImage
        val outTex: Texture

        val rx = image.width / sizeX.toFloat()
        val ry = image.height / sizeY.toFloat()

        if (settings.pixelateBlendSmooth < 1) {
            // pixelate

            if (settings.pixelateScaleType == ScaleType.NEAREST && !settings.pixelateIgnoreBgColor) {
                // simple nearest filter
                ret = ImageUtils.getFilteredImage(image, sizeX, sizeY, FilterType.NEAREST)
                outTex = Texture(ret)
            } else {
                // some more complex filter
                ret = ImageUtils.createEmptyImage(sizeX, sizeY);
                outTex = Texture(ret)
                for (y in 0 until sizeX) {
                    for (x in 0 until sizeY) {
                        val px = IntRange((rx * x).roundToInt(), (rx * (x + 1)).roundToInt() - 1)
                        val py = IntRange((ry * y).roundToInt(), (ry * (y + 1)).roundToInt() - 1)
                        processPixel(outTex, x, y, inTex, px, py)
                    }
                }
            }
        } else {
            // empty image
            ret = ImageUtils.createEmptyImage(sizeX, sizeY)
            outTex = Texture(ret)
        }

        if (settings.pixelateBlendSmooth > 0) {
            // smooth
            var smoothTex = inTex

            if (settings.pixelateSmoothType == SmoothType.ANISO || settings.pixelateSmoothType == SmoothType.ANISO_BILINEAR) {
                // create smooth texture
                var anisoSize = findAnisoSize(image.width, image.height, sizeX, sizeY)
                smoothTex = Texture(ImageUtils.createEmptyImage(anisoSize.x, anisoSize.y))
                val rxA = image.width / anisoSize.x
                val ryA = image.height / anisoSize.y
                for (y in 0 until smoothTex.height) {
                    for (x in 0 until smoothTex.width) {
                        val px = IntRange(x * rxA, (x + 1) * rxA)
                        val py = IntRange(y * ryA, (y + 1) * ryA)
                        averagePixel(smoothTex, x, y, inTex, px, py)
                    }
                }
            }

            // apply to output
            val bilinear = ((smoothTex.width != outTex.width) && (smoothTex.height != outTex.height) &&
                (settings.pixelateSmoothType == SmoothType.BILINEAR || settings.pixelateSmoothType == SmoothType.ANISO_BILINEAR));
            smoothTex = Texture(ImageUtils.getFilteredImage(smoothTex.img, sizeX, sizeY, if (bilinear) FilterType.BILINEAR else FilterType.NEAREST))
            val blend = settings.pixelateBlendSmooth.toFloat()
            for (y in 0 until sizeY) {
                for (x in 0 until sizeX) {
                    val c = ColorAF(outTex.getPoint(x, y)).lerp(ColorAF(smoothTex.getPoint(x, y)), blend)
                    outTex.setPoint(x, y, c.rgba)
                }
            }
        }

        return ret
    }

    override fun getHelp(): String = "Reduce resolution and number of colors\n" +
            "* scale down - only modifies final resolution when used\n" +
            "* size XY - final resolution\n" +
            "* use middle step - first scale the image with smoothing before pixelating\n" +
            "* middle step scale - scale of middle step image relative to target image\n" +
            "* colors per channel - color reduction, 10 means each channel can have only 10 different values (spaced equally)\n" +
            "* filter type - scaling pixel filter\n" +
            "\t- NEAREST - takes nearest pixel (NEAREST without ignoring color is fastest)\n" +
            "\t- MOST_COLOR - take colors from all pixels, that the current one is generated from and use the most frequent color\n" +
            "* scale color tolerance - when using MOST_COLOR, colors within this range will be considered same, the result is average from these\n" +
            "* ignore BG color - when finding new color, this color will be ignored by all filters\n" +
            "* BG color - specify the ignored color\n" +
            "* Smooth blend ratio - 0 means pixelated, 1 means smooth, 0.5 means blend between pixelated and smooth\n" +
            "* Smooth filter type:\n" +
            "\t - BILINEAR - only bilinear filter\n" +
            "\t - ANISO - before filtering, scales down the map and averages color\n" +
            "\t - ANISO_BILINEAR - combines both (smoothest)"

    private fun validateSettings(image: BufferedImage, sizeX: Int, sizeY: Int) {
        check(sizeX >= 1) { "pixelateSizeX must be >= 1" }
        check(sizeY >= 1) { "pixelateSizeY must be >= 1" }
        check(settings.pixelateColors in 2..256) { "pixelateColors must be > 1 and <= 256" }
        check(settings.pixelateScaleColorTolerance in 0..255) {
            "pixelateScaleColorTolerance must be >= 0 and < 256"
        }
        check(settings.pixelateBlendSmooth in 0.0..1.0) {"pixelateBlendSmooth must be 0..1"}
        check(settings.pixelateMiddleScale > 0.0) {"pixelateMiddleScale must be > 0"}
    }

    /**
     * Process pixel when pixelating
     * @param outTex output texture
     * @param x x position in outTex
     * @param y y position in outTex
     * @param inTex input texture
     * @param px x range of pixels in inTex
     * @param py y range of pixels in inTex
     */
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
        for (i in counts.indices) {
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

    /**
     * Find size by dividing original size by 2, the result is >= new size
     */
    private fun findAnisoSize(width: Int, height: Int, newWidth: Int, newHeight: Int): Vec2i {
        val x = findAnisoSize(width, newWidth)
        val y = findAnisoSize(height, newHeight)
        return Vec2i(x, y)
    }

    private fun findAnisoSize(size: Int, newSize: Int): Int {
        var last = size
        while (true) {
            val next = last / 2
            if (next < newSize) {
                return last
            }
            last = next
        }
    }

    private fun averagePixel(outTex: Texture, x: Int, y: Int, inTex: Texture, px: IntRange, py: IntRange) {
        var r = 0.0
        var g = 0.0
        var b = 0.0
        var a = 0

        var ca = 0.0
        var count = 0

        for (iy in py) {
            for (ix in px) {
                val ix = ix % inTex.width
                val iy = iy % inTex.height
                val c = inTex.getPoint(ix, iy)
                val alpha = ColorUtils.getAlpha(c)
                val da = alpha / 255.0

                r += ColorUtils.getRed(c) * da
                g += ColorUtils.getGreen(c) * da
                b += ColorUtils.getBlue(c) * da
                a += alpha

                ca += da
                count++
            }
        }

        var color = 0;
        if (ca > 0) {
            val newR = (r / ca).roundToInt()
            val newG = (g / ca).roundToInt()
            val newB = (b / ca).roundToInt()
            val alpha = (ca * 255 / count).roundToInt()
            color = ColorUtils.fromRGBA(newR, newG, newB, alpha)
        }
        outTex.setPoint(x, y, color)
    }

    private fun middle(r: IntRange) = (r.first + r.last) / 2

    private fun middle(r: Vec2f) = ((r.x + r.y) / 2f).roundToInt()

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

    private fun doMiddleStep(img: BufferedImage, sizeX: Int, sizeY: Int): BufferedImage {
        val smoothBlend = settings.pixelateBlendSmooth
        try {
            settings.pixelateBlendSmooth = 1.0
            val sx = (sizeX * settings.pixelateMiddleScale).roundToInt()
            val sy = (sizeY * settings.pixelateMiddleScale).roundToInt()
            return pixelateImage(img, sx, sy)
        } finally {
            settings.pixelateBlendSmooth = smoothBlend
        }
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
