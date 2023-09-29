package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.image.BufferedImage
import kotlin.random.Random

/**
 * Copies side pixels to oposite edge with ratio.
 */
class SeamlessCommand(settings: Settings) : AbstractCommand(settings) {
    override fun execute(image: BufferedImage): BufferedImage {

        val d = settings.seamlessDist
        val w = image.width
        val h = image.height

        if (d <= 0) {
            throw IllegalArgumentException("seamlessDist must be > 0")
        }
        if (settings.seamlessOverlap && (d * 2 > w || d * 2 > h)) {
            throw IllegalArgumentException("For overlap, seamlessDist must be <= half image width or height")
        }

        var src = Texture(image)
        
        val ret = if (settings.seamlessOverlap) {
            applySeamlessOverlap(image, src)
        } else {
            applySeamlessMirror(image, src)
        }
        return ret.img
    }

    private fun applySeamlessOverlap(image: BufferedImage, src: Texture): Texture {
        val d = settings.seamlessDist
        val w = image.width
        val h = image.height

        val wd = w - d
        val hd = h - d

        val ret = Texture(ImageUtils.createEmptyImage(wd, hd))

        for (y in 0 until hd) {
            for (x in 0 until wd) {
                var c = src.getPoint(x, y)
                if (x < d) {
                    // left
                    c = ColorUtils.lerp(src.getPoint(wd + x, y), c, (x) / (d).toDouble())
                    if (y < d) {
                        // corner
                        val c2 = ColorUtils.lerp(src.getPoint(wd + x, hd + y), src.getPoint(x, hd + y), (x) / (d).toDouble())
                        c = ColorUtils.lerp(c2, c, (y) / (d).toDouble())
                    }
                } else if (y < d) {
                    // up
                    c = ColorUtils.lerp(src.getPoint(x, hd + y), c, (y) / (d).toDouble())
                }
                ret.setPoint(x, y, c)
            }
        }

        return ret
    }

    private fun applySeamlessMirror(image: BufferedImage, src: Texture): Texture {
        val d = settings.seamlessDist
        val w = image.width
        val h = image.height

        val wd = w - d
        val hd = h - d

        var src1 = src
        var ret = Texture(ImageUtils.copyImage(image))

        // left
        for (y in 0 until h) {
            for (x in 0 until d) {
                processEdge(src1, x, y, ret, w - x - 1, y, x, d)
            }
        }
        // right
        for (y in 0 until h) {
            for (x in wd until w) {
                processEdge(src1, x, y, ret, w - x - 1, y, d - x + wd - 1, d)
            }
        }

        src1 = ret
        ret = Texture(ImageUtils.copyImage(src1.img))

        // top
        for (y in 0 until d) {
            for (x in 0 until w) {
                processEdge(src1, x, y, ret, x, h - y - 1, y, d)
            }
        }
        // bottom
        for (y in hd until h) {
            for (x in 0 until w) {
                processEdge(src1, x, y, ret, x, h - y - 1, d - y + hd - 1, d)
            }
        }
        return ret
    }

    override fun getHelp(): String = "Generates seamless texture by repeating a part of it on the opposite edge.\n" +
            "* Distance PX - size of copied edge\n" +
            "* Alpha blending - true averages between original and copied edge, false copies pixels using dithering"

    private fun processEdge(
        src: Texture,
        x: Int,
        y: Int,
        ret: Texture,
        x2: Int,
        y2: Int,
        curr: Int,
        max: Int
    ) {
        val r = getRatio(curr, max)
        if (!settings.seamlessAlpha) {
            if (Random.nextDouble() <= r) {
                val c = src.getPoint(x2, y2)
                ret.setPoint(x, y, c)
            }
        } else {
            val c = src.getPoint(x2, y2)
            val c2 = ret.getPoint(x, y)
            ret.setPoint(x, y, ColorUtils.lerp(c2, c, r))
        }
    }

    /** Computes ratio, 1 - curr color, 0 - other color */
    private fun getRatio(x: Int, d: Int) = 0.5 - (x + 1) / (d * 2 + 1).toDouble()
}
