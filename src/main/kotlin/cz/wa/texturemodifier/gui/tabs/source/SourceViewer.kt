package cz.wa.texturemodifier.gui.tabs.source

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer

/**
 * Displays original texture
 */
class SourceViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        MainFrame.instance!!.addImageOpenListener(createImageListener())
    }
}
