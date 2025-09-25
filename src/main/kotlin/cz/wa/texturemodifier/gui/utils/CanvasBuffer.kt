package cz.wa.texturemodifier.gui.utils

import java.awt.Component
import java.awt.Graphics
import java.awt.image.BufferedImage

class CanvasBuffer(val component: Component) {

	private var canvas: BufferedImage? = null

	fun start(): Graphics {
		if (canvas == null || canvas!!.width != component.width || canvas!!.height != component.width) {
			canvas = ImageUtils.createEmptyImage(component.width, component.height)
		}
		return canvas!!.graphics
	}

	fun finish() {
		if (canvas == null) {
			throw IllegalStateException("canvas is not created")
		}
		component.graphics.drawImage(canvas, 0, 0, null)
	}

	fun destroy() {
		canvas = null
	}
}
