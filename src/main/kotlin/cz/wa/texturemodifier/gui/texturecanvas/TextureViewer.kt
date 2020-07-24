package cz.wa.texturemodifier.gui.texturecanvas

import com.sun.javafx.geom.Vec2d
import cz.wa.texturemodifier.gui.ContentHolder
import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.gui.utils.CanvasBuffer
import cz.wa.texturemodifier.gui.utils.GuiUtils
import cz.wa.texturemodifier.math.Vec2i
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

/**
 * Displays texture. Takes care of moving, zooming, mapping positions.
 */
open class TextureViewer(val contentHolder: ContentHolder) : Canvas(),
    MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    val zoomSpeed = 1.1

    protected var drawInfo = true
    protected var imageSource = ImageSource.SOURCE
    protected var customImage: BufferedImage? = null
    protected var customSize: Dimension? = null

    var posX = 0.0
    var posY = 0.0
    private var lastX = 0
    private var lastY = 0
    protected var mouseRDown = false
    protected var mouseLDown = false
    protected var mouseInside = false
    var zoom = 1.0
    protected var currMousePos = Vec2i.NEGATIVE

    // settings
    protected var infoBgColor = Color.GRAY
    protected var infoTextColor = Color.BLACK
    protected var infoGap = 2
    protected var infoFontSize = 12
    protected var infoFont = Font("Courier new", Font.PLAIN, infoFontSize)
    protected var infoWidth = 300

    private val canvasBuffer = CanvasBuffer(this)

    init {
        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
        addKeyListener(this)

        minimumSize = Dimension(16, 16)
        maximumSize = Dimension(4096, 4096)
    }

    private fun paintComponent(g: Graphics) {
        g.color = contentHolder.settings.guiBgColor
        g.fillRect(0, 0, width, height)
        if (contentHolder.sourceImage == null) {
            return
        }
        drawBefore(g)
        drawSourceImage(g)
        drawAfter(g)
        if (mouseInside && drawInfo) {
            drawTileInfo(g)
        }
    }

    protected open fun drawSourceImage(g: Graphics) {
        val img = when (imageSource) {
            ImageSource.SOURCE -> contentHolder.sourceImage!!
            ImageSource.OUTPUT -> contentHolder.outputImage!!
            else -> customImage
        }
        if (img != null) {
            drawImage(img, g)
        }
    }

    protected fun drawImage(img: BufferedImage, g: Graphics) {
        drawImage(img, 0, 0, g)
    }

    protected fun drawImage(img: BufferedImage, x: Int, y: Int, g: Graphics) {
        val p1 = imgToScr(Vec2i(x, y))
        val p2 = Vec2i(imgToScr(img.width), imgToScr(img.height))
        g.drawImage(img, p1.x, p1.y, p2.x, p2.y, null)
    }

    protected open fun drawBefore(g: Graphics) {
        // empty
    }

    protected open fun drawAfter(g: Graphics) {
        // empty
    }

    protected open fun drawTileInfo(g: Graphics) {
        val text = "coords: ${scrToImg(currMousePos)}"
        g.font = infoFont

        g.color = infoBgColor
        val gap2 = 2 * infoGap
        g.fillRect(0, height - infoFontSize - gap2, infoWidth, infoFontSize + gap2)

        g.color = infoTextColor
        g.drawString(text, infoGap, height - infoGap)
    }

    protected fun getImageWidth(): Int {
        return when(imageSource) {
            ImageSource.SOURCE -> contentHolder.sourceImage!!.width
            ImageSource.OUTPUT -> contentHolder.outputImage!!.width
            ImageSource.CUSTOM -> customSize!!.width
        }
    }

    protected fun getImageHeight(): Int {
        return when(imageSource) {
            ImageSource.SOURCE -> contentHolder.sourceImage!!.height
            ImageSource.OUTPUT -> contentHolder.outputImage!!.height
            ImageSource.CUSTOM -> customSize!!.height
        }
    }

    protected fun scrToImg(p: Vec2i): Vec2i {
        if (contentHolder.sourceImage == null) {
            return Vec2i.NEGATIVE
        }
        val x = (p.x - width / 2.0) / zoom
        val y = (p.y - height / 2.0) / zoom
        return Vec2i(
            Vec2d(
                x - posX + getImageWidth() / 2.0,
                y - posY + getImageHeight() / 2.0
            )
        )
    }

    protected fun imgToScr(p: Vec2i): Vec2i {
        return imgToScr(p.toDouble())
    }

    protected fun imgToScr(p: Vec2d): Vec2i {
        if (contentHolder.sourceImage == null) {
            return Vec2i.ZERO
        }
        val x = p.x + posX - getImageWidth() / 2.0
        val y = p.y + posY - getImageHeight() / 2.0
        return Vec2i((x * zoom + width / 2.0).roundToInt(), (y * zoom + height / 2.0).roundToInt())
    }

    protected fun scrToImg(x: Int): Int {
        return (x / zoom).toInt()
    }

    protected fun imgToScr(x: Double): Int {
        return (x * zoom).roundToInt()
    }

    protected fun imgToScr(x: Int): Int {
        return imgToScr(x.toDouble())
    }

    override fun mouseEntered(e: MouseEvent) {
        mouseInside = true
    }

    override fun mouseExited(e: MouseEvent) {
        mouseInside = false
    }

    override fun mousePressed(e: MouseEvent) {
        if (e.button == MouseEvent.BUTTON1) {
            mouseLDown = true
        } else if (e.button == MouseEvent.BUTTON3) {
            mouseRDown = true
            lastX = e.x
            lastY = e.y
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        if (e.button == MouseEvent.BUTTON1) {
            mouseLDown = false
        } else if (e.button == MouseEvent.BUTTON3) {
            mouseRDown = false
        }
    }

    override fun mouseClicked(e: MouseEvent) {
        // empty
    }

    override fun mouseMoved(e: MouseEvent) {
        lastX = e.x
        lastY = e.y
        currMousePos = Vec2i(e.x, e.y)
        if (drawInfo) {
            drawTileInfo(graphics)
        }
    }

    override fun mouseDragged(e: MouseEvent) {
        if (mouseRDown) {
            posX += (e.x - lastX) / zoom
            posY += (e.y - lastY) / zoom
            refresh()
        }
        lastX = e.x
        lastY = e.y
        currMousePos = Vec2i(e.x, e.y)
        if (drawInfo) {
            drawTileInfo(graphics)
        }
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        val rotated = e!!.wheelRotation
        if (rotated > 0) {
            zoom /= zoomSpeed
            refresh()
        } else if (rotated < 0) {
            zoom *= zoomSpeed
            refresh()
        }
    }

    override fun keyTyped(e: KeyEvent) {
        // empty
    }

    override fun keyPressed(e: KeyEvent) {
        GuiUtils.runCatch(this, Runnable {
            if (e.keyCode == KeyEvent.VK_HOME) {
                zoom = 1.0
                posX = 0.0
                posY = 0.0
                refresh()
            }
            // save/load
            if (e.isControlDown) {
                if (e.keyCode == KeyEvent.VK_O) {
                    MainFrame.instance!!.openImage()
                } else if (e.keyCode == KeyEvent.VK_S) {
                    MainFrame.instance!!.saveImage()
                } else if (e.keyCode == KeyEvent.VK_L) {
                    MainFrame.instance!!.quickOpenImage()
                } else if (e.keyCode == KeyEvent.VK_R) {
                    MainFrame.instance!!.reloadImage()
                } else if (e.keyCode == KeyEvent.VK_P) {
                    MainFrame.instance!!.openProperties()
                }
            }
        })
    }

    override fun keyReleased(e: KeyEvent) {
        // empty
    }

    /** Redraw component using buffer, used when user drew/selected something */
    fun refresh() {
        val g = canvasBuffer.start()
        paintComponent(g)
        canvasBuffer.finish()
        g.dispose()
    }

    /** Redraw component without buffer, used by swing */
    override fun paint(g: Graphics) {
        paintComponent(g)
    }

    protected enum class ImageSource {
        SOURCE,
        OUTPUT,
        CUSTOM
    }
}
