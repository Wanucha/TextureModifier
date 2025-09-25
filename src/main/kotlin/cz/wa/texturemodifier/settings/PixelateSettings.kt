package cz.wa.texturemodifier.settings

import java.awt.Color

data class PixelateSettings(
	var scale: Double = 4.0,
	var useSize: Boolean = false,
	var sizeX: Int = 0,
	var sizeY: Int = 0,
	var colors: Int = 256,
	var middleUse: Boolean = false,
	var middleScale: Double = 2.0,
	var scaleType: ScaleType = ScaleType.NEAREST,
	var scaleColorTolerance: Int = 5,
	var ignoreBgColor: Boolean = false,
	var backgroundColor: Color = Color.BLACK,
	var blendSmooth: Double = 0.0,
	var smoothType: SmoothType = SmoothType.ANISO_BILINEAR,
)
