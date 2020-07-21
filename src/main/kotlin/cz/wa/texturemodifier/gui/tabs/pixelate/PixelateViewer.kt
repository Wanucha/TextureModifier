package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer

/**
 * Displays pixelated texture
 */
class PixelateViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        imageSource = ImageSource.OUTPUT
    }
}
