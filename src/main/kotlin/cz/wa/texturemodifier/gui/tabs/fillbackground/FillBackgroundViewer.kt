package cz.wa.texturemodifier.gui.tabs.blur

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer

class FillBackgroundViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        imageSource = ImageSource.OUTPUT
    }
}
