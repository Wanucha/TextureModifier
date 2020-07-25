package cz.wa.texturemodifier.gui.tabs.source

import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.texturecanvas.TextureViewer
import java.io.File

/**
 * Displays original texture
 */
class SourceViewer(contentHolder: ContentHolder) : TextureViewer(contentHolder) {
    init {
        val fol = object: MainFrame.FileOpenListener {
            override fun fileOpened(file: File) {
                refresh()
            }
        }
        MainFrame.instance!!.addImageOpenListener(fol)
    }
}
