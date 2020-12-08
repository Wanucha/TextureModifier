package cz.wa.texturemodifier.gui.tabs

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import cz.wa.texturemodifier.math.Vec2i

/**
 * Also displays alpha
 */
class ModifierAlphaViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        imageSource = ImageSource.OUTPUT
        val l = createImageListener()
        MainFrame.instance!!.addImageOpenListener(l)
        MainFrame.instance!!.addImageRevertListener(l)
    }

    override fun getInfoText(p: Vec2i): String {
        var text = "coords: $p"

        val image = getImage()
        if (image != null) {
            val tex = Texture(image)
            if (p.x >= 0 && p.x < tex.width && p.y >= 0 && p.y < tex.height) {
                val color = tex.getPoint(p.x, p.y)
                text += ", color: ${ColorUtils.toStringWithAlpha(color)}"
            }
        }
        return text
    }
}
