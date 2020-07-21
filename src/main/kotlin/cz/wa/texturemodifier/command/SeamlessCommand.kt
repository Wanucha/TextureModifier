package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.image.BufferedImage
import kotlin.random.Random

/**
 * Copies side pixels to oposite edge with ratio.
 */
class SeamlessCommand(settings: Settings) : AbstractCommand(settings) {
    override fun execute(image: BufferedImage): BufferedImage {
        if (settings.seamlessDist <= 0) {
            throw IllegalArgumentException("seamlessDist must be > 0")
        }

        val d = settings.seamlessDist
        val d2 = d * 2
        val w = image.width
        val h = image.height
        val wd = w - d
        val hd = h - d

        var src = image
        var ret = ImageUtils.copyImage(src)

        // left
        for (y in 0 until h) {
            for (x in 0 until d) {
                processEdge(src, x, y, ret, w - x - 1, y, x, d)
            }
        }
        // right
        for (y in 0 until h) {
            for (x in wd until w) {
                processEdge(src, x, y, ret, w - x - 1, y, d - x + wd - 1, d)
            }
        }

        src = ret
        ret = ImageUtils.copyImage(src)

        // top
        for (y in 0 until d) {
            for (x in 0 until w) {
                processEdge(src, x, y, ret, x, h - y - 1, y, d)
            }
        }
        // bottom
        for (y in hd until h) {
            for (x in 0 until w) {
                processEdge(src, x, y, ret, x, w - y - 1, d - y + hd - 1, d)
            }
        }
        return ret
    }

    private fun processEdge(
        src: BufferedImage,
        x: Int,
        y: Int,
        ret: BufferedImage,
        x2: Int,
        y2: Int,
        curr: Int,
        max: Int
    ) {
        val r = getRatio(curr, max)
        if (!settings.seamlessAlpha) {
            if (Random.nextDouble() <= r) {
                val c = src.getRGB(x2, y2)
                ret.setRGB(x, y, c)
            }
        } else {
            val c = src.getRGB(x2, y2)
            val c2 = ret.getRGB(x, y)
            ret.setRGB(x, y, ColorUtils.lerp(c2, c, r))
        }
    }

    /** Computes ratio, 1 - curr color, 0 - other color */
    private fun getRatio(x: Int, d: Int) = 0.5 - (x + 1) / (d * 2 + 1).toDouble()
}