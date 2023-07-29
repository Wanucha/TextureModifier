package cz.wa.texturemodifier.gui.utils

import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
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

        fun getImageWithIntBuffer(img: BufferedImage) : BufferedImage {
            try {
                if (img.raster.dataBuffer is DataBufferInt) {
                    return img
                } else {
                    return convertToIntBuffer(img)
                }
            } catch (e: Exception) {
                throw RuntimeException("Failed to convert image buffer, try saving the image in different program.", e)
            }
        }

        fun getFilteredImage(img: BufferedImage, sizeX: Int, sizeY: Int, filter: FilterType): BufferedImage {
            val scaledImage = BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB)
            val at = AffineTransform.getScaleInstance(sizeX / img.width.toDouble(), sizeY / img.height.toDouble())
            val ato = AffineTransformOp(at, filter.value)
            return ato.filter(img, scaledImage)
        }

        private fun convertToIntBuffer(img: BufferedImage): BufferedImage {
            val ret = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
            val outData = (ret.raster.dataBuffer as DataBufferInt).data
            val data = (img.raster.dataBuffer as DataBufferByte).data

            val length = img.width * img.height
            if (length * 4 == data.size && img.type != BufferedImage.TYPE_BYTE_INDEXED) {
                for (i in 0 until length) {
                    val i2 = i * 4
                    outData[i] =
                        (data[i2].toInt() shl 24) or (data[i2 + 1].toInt() shl 16) or (data[i2 + 2].toInt() shl 8) or (data[i2 + 3].toInt())
                }
            } else {
                for (i in 0 until length) {
                    // TODO slow!!!
                    outData[i] = img.getRGB(i % img.width, i / img.width)
                }
            }
            return ret
        }
    }
}
