package cz.wa.texturemodifier.gui.utils

import cz.wa.texturemodifier.gui.ContentHolder
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage
import javax.swing.JPanel
import kotlin.math.roundToInt

/**
 * Slider for color, currently changes bg color
 */
class ColorSlider(val contentHolder: ContentHolder) : JPanel(), MouseListener, MouseMotionListener {
    var color1: Color = Color.BLACK
    var color2: Color = Color.WHITE
    var valueColor = Color.RED

    private var bgImage: BufferedImage = generateBgImage()

    private var mouseDown = false

    var value = 0
        set(value) {
            field = value.coerceIn(0, 255)
            refresh()
        }

    init {
        addMouseListener(this)
        addMouseMotionListener(this)
        cursor = Cursor(Cursor.E_RESIZE_CURSOR)
    }

    private fun generateBgImage(): BufferedImage {
        val ret = ImageUtils.createEmptyImage(256, 1)
        for (i in 0..255) {
            val c = getColorAt(i)
            ret.setRGB(i, 0, c.rgb)
        }
        return ret
    }

    private fun getColorAt(i: Int): Color {
        val d2 = i / (256f * 256f)
        val d1 = (1 - i / 256f) / 256f
        val c = Color(
            color1.red * d1 + color2.red * d2,
            color1.green * d1 + color2.green * d2,
            color1.blue * d1 + color2.blue * d2
        )
        return c
    }

    override fun paint(g: Graphics) {
        // colors
        g.drawImage(bgImage, 0, 0, width, height, 0, 0, bgImage.width, bgImage.height, null)
        // value
        val x = (value * width / 256f).roundToInt()
        g.color = valueColor
        g.drawLine(x, 0, x, height)
    }

    fun refresh() {
        paint(graphics)
    }

    override fun mousePressed(e: MouseEvent) {
        mouseDown = true
        mouseMoved(e)
    }

    override fun mouseReleased(e: MouseEvent) {
        mouseDown = false
        contentHolder.settings.guiBgColor = getColorAt(value)
    }

    override fun mouseEntered(e: MouseEvent?) {
        // empty
    }

    override fun mouseClicked(e: MouseEvent?) {
        // empty
    }

    override fun mouseExited(e: MouseEvent?) {
        // empty
    }

    override fun mouseMoved(e: MouseEvent) {
        if (mouseDown) {
            value = (256f * (e.x / width.toFloat())).roundToInt()
            refresh()
        }
    }

    override fun mouseDragged(e: MouseEvent) {
        mouseMoved(e)
    }
}