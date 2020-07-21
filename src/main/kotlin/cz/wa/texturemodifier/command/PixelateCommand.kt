package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.Settings
import java.awt.image.BufferedImage

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
        check(settings.pixelateScale > 1) { throw IllegalArgumentException("pixelateScale must be > 1") }
        check(settings.pixelateColors > 1 && settings.pixelateColors <= 256) { throw IllegalArgumentException("pixelateColors must be > 1 and <= 256") }
        check(settings.pixelateScaleColorTolerance >= 0 && settings.pixelateScaleColorTolerance < 256) {
            throw IllegalArgumentException(
                "pixelateScaleColorTolerance must be >= 0 and < 256"
            )
        }
        TODO()
    }
}