package cz.wa.texturemodifier.gui.help

import java.awt.BorderLayout
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane

class HelpFrame: JFrame("Texture Modifier Help") {
    init {
        bounds = Rectangle(200, 200, 540, 620)

        this@HelpFrame.layout = BorderLayout()

        var text1 = JLabel()
        text1.text = "<html>" +
                "There are loaded 3 files:<br>" +
                "* settings<br>" +
                "* input image<br>" +
                "* output image<br>" +
                "<br>" +
                "The settings can be modified, loaded and saved independently.<br>" +
                "When a modifier is applied, its settings are stored to memory (not disk).<br>" +
                "<br>" +
                "Input image is the opened image, modifiers cannot change it.<br>" +
                "If you apply some modifier, the input image remains original.<br>" +
                "Next time you apply a modifier, it will be applied to the original image.<br>" +
                "<br>" +
                "Output image is the currently modified image.<br>" +
                "If you open a new image, input and output images will be overwritten by the new one.<br>" +
                "When saving image, saved is always the output.<br>" +
                "<br>" +
                "To apply next modifier to a modified image, switch to source tab and click 'Apply modified'.<br>" +
                "It will copy the output -> input but not write any data to disk.<br>" +
                "<br>" +
                "Image view:<br>" +
                "* Right mouse button - move<br>" +
                "* Mouse wheel - zoom<br>" +
                "* Home - reset view<br>" +
                "<br>" +
                "Bugs:<br>" +
                "* Sometimes the viewed image is not refreshed (when you change bg color).<br>" +
                "Resolution: move or zoom the view.<br>" +
                "* Sometimes the main menu is overdrawn by image view.<br>" +
                "Resolution: switch to properties tab or use shortcuts:<br>" +
                "* Open - ctrl+O<br>" +
                "* Open in direscory - ctrl+L (show list of images in current directory)<br>" +
                "* Save as - ctrl+S (save output image)<br>" +
                "* Reload - ctrl+R (reload from disk)<br>" +
                "* Revert - ctrl+Z (copy input -> output without reloading)<br>" +
                "* Open properties - ctrl+P<br>" +
                "* Save properties as - ctrl+U<br>" +
                "</html>"

        var scroll = JScrollPane(text1)
        add(scroll, BorderLayout.CENTER)
    }
}
