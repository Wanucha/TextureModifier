package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.image.BufferedImage

class RemoveAlphaCommand(settings: Settings) : AbstractCommand(settings) {

    override fun execute(image: BufferedImage): BufferedImage {
        check(settings.removeAlphaThreshold in 0..256) { "removeAlphaThreshold must be 0..256" }

        val ret = ImageUtils.copyImage(image)
        val tex = Texture(ret)

        for (y in 0 until ret.height) {
            for (x in 0 until ret.width) {
                var c = tex.getPoint(x, y)
                val a = ColorUtils.getAlpha(c)
                if (a in 1..254) {
                    c = ColorUtils.setAlpha(c, computeAlpha(a))
                    tex.setPoint(x, y, c)
                }
            }
        }
        return ret
    }

    private fun computeAlpha(a: Int): Int {
        if (a >= settings.removeAlphaThreshold) {
            return 255
        } else {
            return 0
        }
    }

    override fun getHelp(): String = "Removes alpha and replaces with value 0 or 255\n" +
            "* threshold - alpha >= value will be 255, < will be 0"

}