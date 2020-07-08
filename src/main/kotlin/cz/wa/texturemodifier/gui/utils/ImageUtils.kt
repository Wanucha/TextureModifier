package cz.wa.texturemodifier.gui.utils

import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

class ImageUtils {
    companion object {
        fun createEmptyImage(newWidth: Int, newHeight: Int): BufferedImage {
            val ret = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
            val array = getImageArray(ret)
            array.fill(0)
            return ret
        }

        fun copyImage(img: BufferedImage): BufferedImage {
            val ret = createEmptyImage(img.width, img.height)
            ret.graphics.drawImage(img, 0, 0, null)
            return ret
        }

        fun getImageArray(img: BufferedImage): IntArray {
            return (img.getRaster().getDataBuffer() as DataBufferInt).data
        }
    }
}
