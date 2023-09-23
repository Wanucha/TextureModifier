package cz.wa.texturemodifier.gui.tabs

import cz.wa.texturemodifier.gui.ContentHolder
import java.awt.Graphics

/**
 * Adds option to tile image
 */
class TillingMdofierViewer(contentHolder: ContentHolder) : ModifierViewer(contentHolder) {
    var showTilling = false

    override fun drawBounds(g: Graphics) {
        if (!showTilling) {
            super.drawBounds(g)
        }
    }

    override fun drawSourceImage(g: Graphics) {
        if (showTilling) {
            val img = getImage()
            if (img != null) {
                drawImage(img, -img.width, -img.height, g)
                drawImage(img, 0, -img.height, g)
                drawImage(img, img.width, -img.height, g)

                drawImage(img, -img.width, 0, g)
                drawImage(img, g)
                drawImage(img, img.width, 0, g)

                drawImage(img, -img.width, img.height, g)
                drawImage(img, 0, img.height, g)
                drawImage(img, img.width, img.height, g)
            }
        } else {
            super.drawSourceImage(g)
        }
    }
}