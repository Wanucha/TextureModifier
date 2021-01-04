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
        if (settings.seamlessDist <= 0) {
            throw IllegalArgumentException("seamlessDist must be > 0")
        }

        val d = settings.seamlessDist
        val w = image.width
        val h = image.height
        val wd = w - d
        val hd = h - d

        var src = Texture(image)
        var ret = Texture(ImageUtils.copyImage(image))

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
        ret = Texture(ImageUtils.copyImage(src.img))

        // top
        for (y in 0 until d) {
            for (x in 0 until w) {
                processEdge(src, x, y, ret, x, h - y - 1, y, d)
            }
        }
        // bottom
        for (y in hd until h) {
            for (x in 0 until w) {
                processEdge(src, x, y, ret, x, h - y - 1, d - y + hd - 1, d)
            }
        }
        return ret.img
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