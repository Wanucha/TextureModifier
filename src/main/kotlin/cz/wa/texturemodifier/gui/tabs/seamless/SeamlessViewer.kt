package cz.wa.texturemodifier.gui.tabs.seamless

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer

/**
 * Displays seamless texture
 */
class SeamlessViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        imageSource = ImageSource.OUTPUT
    }
}
