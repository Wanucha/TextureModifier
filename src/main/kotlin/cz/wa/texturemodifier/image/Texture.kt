package cz.wa.texturemodifier.image

import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.math.Vec2i
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

/**
 * Creates a texture from image. If the image has different buffer than DataBufferInt, then converts it which can be slow.
 * Wraps the image, does not create any pixel array.
 */
class Texture(img: BufferedImage) {
    val img: BufferedImage
    val size = Vec2i(img.width, img.height)

    val width = size.x
    val height = size.y

    private val data: IntArray

    init {
        this.img = ImageUtils.getImageWithIntBuffer(img)
        data = (this.img.raster.dataBuffer as DataBufferInt).data
    }

    /** Get color from exact position */
    fun getPoint(x: Int, y: Int): Int {
        return data[size.x * y + x]
    }

    fun setPoint(x: Int, y: Int, rgb: Int) {
        data[size.x * y + x] = rgb
    }

    /** Get color from ratio position <0..1) */
    fun getPoint(x: Float, y: Float): Int {
        var ix = (x * size.x).toInt()
        val iy = (y * size.y).toInt()
        if (ix == size.x) {
            ix--
        }
        return getPoint(ix, iy)
    }
}