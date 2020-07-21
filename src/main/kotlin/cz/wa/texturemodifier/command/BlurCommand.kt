package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.MathUtils
import cz.wa.texturemodifier.image.ColorF
import cz.wa.texturemodifier.image.TextureF
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

/**
 * Blurs image and merges with original. Takes texture as a tile.
 */
class BlurCommand(settings: Settings) : AbstractCommand(settings) {


    override fun execute(image: BufferedImage): BufferedImage {
        check(settings.blurRadius > 1) { throw IllegalArgumentException("blurRadius must be > 1") }
        check(settings.blurRatio > 0 && settings.blurRatio <= 1) { throw IllegalArgumentException("blurRatio must be > 0 and <= 1") }

        val core = generateCore(settings.blurRadius)
        val orig = TextureF.createTexture(image)
        var tmp = TextureF(orig)
        val blured = TextureF(tmp.width, tmp.height)

        // blur x
        for (y in 0 until tmp.height) {
            for (x in 0 until tmp.width) {
                val c = blurPixel(tmp, core, x, y, true)
                blured.pixels[x][y] = c
            }
        }

        // blur y
        tmp = TextureF(blured)
        for (y in 0 until tmp.height) {
            for (x in 0 until tmp.width) {
                val c = blurPixel(tmp, core, x, y, false)
                blured.pixels[x][y] = c
            }
        }

        // merge
        if (settings.blurRatio < 1) {
            val ratio = settings.blurRatio.toFloat()
            for (y in 0 until tmp.height) {
                for (x in 0 until tmp.width) {
                    val c = orig.pixels[x][y].lerp(blured.pixels[x][y], ratio)
                    blured.pixels[x][y] = c
                }
            }
        }

        return blured.generateImage()
    }

    private fun blurPixel(source: TextureF, core: FloatArray, x: Int, y: Int, dirX: Boolean): ColorF {
        var r = 0f
        var g = 0f
        var b = 0f

        val d = core.size / 2
        for (i in 0 until core.size) {
            val sx = if(dirX)  MathUtils.mod(x + i - d, source.width) else x
            val sy = if(!dirX)  MathUtils.mod(y + i - d, source.height) else y
            val c = source.pixels[sx][sy]
            val cr = core[i]
            r += c.r * cr
            g += c.g * cr
            b += c.b * cr
        }
        return ColorF(r, g, b)
    }

    private fun generateCore(radius: Double): FloatArray {
        var size = (radius * 2 + 0.25).roundToInt()
        if (size % 2 == 0) {
            size++
        }
        val r = size / 2

        // generate gaussian
        val ret = FloatArray(size)
        for (i in 0..r) {
            val v = MathUtils.gauss(radius * SIGMA_MULTIP, i.toDouble())
            ret[r + i] = v.toFloat()
            ret[r - i] = ret[r + i]
        }

        // normalize so sum = 1
        val sum = ret.sum()
        for (i in 0 until ret.size) {
            ret[i] /= sum
        }
        return ret
    }

    companion object {
        const val SIGMA_MULTIP = 1 / 3.0
    }
}