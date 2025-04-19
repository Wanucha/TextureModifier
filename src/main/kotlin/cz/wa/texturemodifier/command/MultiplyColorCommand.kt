package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import cz.wa.texturemodifier.settings.Settings
import java.awt.image.BufferedImage
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Multiplies by a color, then adds different color.
 */
class MultiplyColorCommand(settings: Settings) : AbstractCommand(settings) {

    override fun execute(image: BufferedImage): BufferedImage {
        val ret = ImageUtils.copyImage(image)
        val outTex = Texture(ret)

        val r1 = settings.multiplyColorMulColor.red / 255.0
        val g1 = settings.multiplyColorMulColor.green / 255.0
        val b1 = settings.multiplyColorMulColor.blue / 255.0

        val r2 = settings.multiplyColorAddColor.red
        val g2 = settings.multiplyColorAddColor.green
        val b2 = settings.multiplyColorAddColor.blue

        for (y in 0 until ret.height) {
            for (x in 0 until ret.width) {
                val c = outTex.getPoint(x, y)
                val r = min(255, (ColorUtils.getRed(c) * r1 + r2).roundToInt())
                val g = min(255, (ColorUtils.getGreen(c) * g1 + g2).roundToInt())
                val b = min(255, (ColorUtils.getBlue(c) * b1 + b2).roundToInt())
                outTex.setPoint(x, y, ColorUtils.fromRGB(r, g, b))
            }
        }

        return ret
    }

    override fun getHelp(): String = "Multyply by color and add color\n" +
            "* multiply color - 1. multiplies by this color\n" +
            "* add color - 2. adds this color\n"
}
