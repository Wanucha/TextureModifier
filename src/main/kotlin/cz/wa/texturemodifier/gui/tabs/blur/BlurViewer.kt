package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer

/**
 * Displays blur texture
 */
class BlurViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        imageSource = ImageSource.OUTPUT
    }
}
