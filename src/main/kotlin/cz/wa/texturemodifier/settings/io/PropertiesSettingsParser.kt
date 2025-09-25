package cz.wa.texturemodifier.settings.io

import cz.wa.texturemodifier.math.ColorUtils
import cz.wa.texturemodifier.settings.MapType
import cz.wa.texturemodifier.settings.OverwriteType
import cz.wa.texturemodifier.settings.ScaleType
import cz.wa.texturemodifier.settings.Settings
import cz.wa.texturemodifier.settings.SmoothType
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Properties

/**
 * Legacy properties parser, cannot save to properties (use YML)
 */
object PropertiesSettingsParser {
	private const val GUI_BG_COLOR = "gui-bg-color"
	private const val GUI_SHOW_BOUNDS = "gui-show_bounds"
	private const val OUT_PREFIX = "out-prefix"
	private const val OUT_POSTFIX = "out-postfix"
	private const val OUT_FORMAT = "out-format"
	private const val OVERWRITE_TYPE = "overwrite-type"
	private const val SEAMLESS_DIST = "seamless-dist"
	private const val SEAMLESS_ALPHA = "seamless-alpha"
	private const val SEAMLESS_OVERLAP = "seamless-overlap"
	private const val BLUR_RADIUS = "blur-radius"
	private const val BLUR_RATIO = "blur-ratio"
	private const val PIXELATE_SCALE = "pixelate-scale"
	private const val PIXELATE_USE_SIZE = "pixelate-use-size"
	private const val PIXELATE_SIZE_X = "pixelate-size-x"
	private const val PIXELATE_SIZE_Y = "pixelate-size-y"
	private const val PIXELATE_COLORS = "pixelate-colors"
	private const val PIXELATE_MIDDLE_USE = "pixelate-middle-use"
	private const val PIXELATE_MIDDLE_SCALE = "pixelate-middle-scale"
	private const val PIXELATE_SCALE_TYPE = "pixelate-scale-type"
	private const val PIXELATE_SCALE_COLOR_TOLERANCE = "pixelate-scale-color-tolerance"
	private const val PIXELATE_IGNORE_BG_COLOR = "pixelate-ignore-bg-color"
	private const val PIXELATE_BG_COLOR = "pixelate-bg-color"
	private const val PIXELATE_BLEND_SMOOTH = "pixelate-blend-smooth"
	private const val PIXELATE_SMOOTH_TYPE = "pixelate-smooth-type"
	private const val FILL_BG_ITERATIONS = "fillbg-iterations"
	private const val FILL_BG_INCLUDE_CORNERS = "fillbg-include-corners"
	private const val FILL_BG_AVERAGE_FILL = "fillbg-average-fill"
	private const val FILL_BG_BG_COLOR = "fillbg-bg-color"
	private const val MERGE_MAPS_LAYOUT = "merge-maps-layout"
	private const val MERGE_MAPS_MAP1 = "merge-maps-map1"
	private const val MERGE_MAPS_MAP2 = "merge-maps-map2"
	private const val MERGE_MAPS_MAP3 = "merge-maps-map3"
	private const val MERGE_MAPS_MAP4 = "merge-maps-map4"
	private const val MULTIPLY_COLOR_MUL_COLOR = "multiply-color-mul-color"
	private const val MULTIPLY_COLOR_ADD_COLOR = "multiply-color-add-color"
	private const val REMOVE_ALPHA_THRESHOLD = "remove-alpha-threshold"

	fun parseFile(file: File): Settings {
		return parseString(file.readText())
	}

	fun parseString(text: String): Settings {
		val p = Properties()
		p.load(ByteArrayInputStream(text.toByteArray(Charsets.UTF_8)))
		val ret = Settings()
		var i = 0
		for (entry in p) {
			i++
			try {
				parseLine(entry, ret)
			} catch (e: Exception) {
				throw IllegalArgumentException("Error parsing line ${i}, key = ${entry.key}, value = ${entry.value}", e)
			}
		}
		if (ret.output.prefix.isEmpty() && ret.output.postfix.isEmpty()) {
			throw IllegalArgumentException("$OUT_PREFIX and $OUT_POSTFIX must not be both empty")
		}
		return ret
	}

	private fun parseLine(entry: MutableMap.MutableEntry<Any, Any>, ret: Settings) {
		if (entry.key == GUI_BG_COLOR) {
			ret.gui.backgroundColor = parseColor(entry)
		}
		if (entry.key == GUI_SHOW_BOUNDS) {
			ret.gui.showBounds = parseBool(entry)
		}
		if (entry.key == OUT_PREFIX) {
			ret.output.prefix = parseString(entry)
		}
		if (entry.key == OUT_POSTFIX) {
			ret.output.postfix = parseString(entry)
		}
		if (entry.key == OUT_FORMAT) {
			ret.output.format = parseString(entry)
		}
		if (entry.key == OVERWRITE_TYPE) {
			ret.output.overwriteType = parseEnum(entry, OverwriteType::class.java)
		}
		if (entry.key == SEAMLESS_DIST) {
			ret.seamless.distance = parseInt(entry)
		}
		if (entry.key == SEAMLESS_ALPHA) {
			ret.seamless.alpha = parseBool(entry)
		}
		if (entry.key == SEAMLESS_OVERLAP) {
			ret.seamless.overlap = parseBool(entry)
		}
		if (entry.key == BLUR_RADIUS) {
			ret.blur.radius = parseDouble(entry)
		}
		if (entry.key == BLUR_RATIO) {
			ret.blur.ratio = parseDouble(entry)
		}
		if (entry.key == PIXELATE_SCALE) {
			ret.pixelate.scale = parseDouble(entry)
		}
		if (entry.key == PIXELATE_USE_SIZE) {
			ret.pixelate.useSize = parseBool(entry)
		}
		if (entry.key == PIXELATE_SIZE_X) {
			ret.pixelate.sizeX = parseInt(entry)
		}
		if (entry.key == PIXELATE_SIZE_Y) {
			ret.pixelate.sizeY = parseInt(entry)
		}
		if (entry.key == PIXELATE_COLORS) {
			ret.pixelate.colors = parseInt(entry)
		}
		if (entry.key == PIXELATE_MIDDLE_USE) {
			ret.pixelate.middleUse = parseBool(entry)
		}
		if (entry.key == PIXELATE_MIDDLE_SCALE) {
			ret.pixelate.middleScale = parseDouble(entry)
		}
		if (entry.key == PIXELATE_SCALE_TYPE) {
			ret.pixelate.scaleType = parseEnum(entry, ScaleType::class.java)
		}
		if (entry.key == PIXELATE_SCALE_COLOR_TOLERANCE) {
			ret.pixelate.scaleColorTolerance = parseInt(entry)
		}
		if (entry.key == PIXELATE_IGNORE_BG_COLOR) {
			ret.pixelate.ignoreBgColor = parseBool(entry)
		}
		if (entry.key == PIXELATE_BG_COLOR) {
			ret.pixelate.backgroundColor = parseColor(entry)
		}
		if (entry.key == PIXELATE_BLEND_SMOOTH) {
			ret.pixelate.blendSmooth = parseDouble(entry)
		}
		if (entry.key == PIXELATE_SMOOTH_TYPE) {
			ret.pixelate.smoothType = parseEnum(entry, SmoothType::class.java)
		}
		if (entry.key == FILL_BG_ITERATIONS) {
			ret.fillBackground.iterations = parseInt(entry)
		}
		if (entry.key == FILL_BG_INCLUDE_CORNERS) {
			ret.fillBackground.includeCorners = parseBool(entry)
		}
		if (entry.key == FILL_BG_AVERAGE_FILL) {
			ret.fillBackground.averageFill = parseBool(entry)
		}
		if (entry.key == FILL_BG_BG_COLOR) {
			ret.fillBackground.bgColor = parseColor(entry)
		}
		if (entry.key == MERGE_MAPS_LAYOUT) {
			ret.mergeMaps.layout = parseEnum(entry, MapType::class.java)
		}
		if (entry.key == MERGE_MAPS_MAP1) {
			ret.mergeMaps.map1 = parseString(entry)
		}
		if (entry.key == MERGE_MAPS_MAP2) {
			ret.mergeMaps.map2 = parseString(entry)
		}
		if (entry.key == MERGE_MAPS_MAP3) {
			ret.mergeMaps.map3 = parseString(entry)
		}
		if (entry.key == MERGE_MAPS_MAP4) {
			ret.mergeMaps.map4 = parseString(entry)
		}
		if (entry.key == REMOVE_ALPHA_THRESHOLD) {
			ret.removeAlpha.threshold = parseInt(entry)
		}
		if (entry.key == MULTIPLY_COLOR_MUL_COLOR) {
			ret.multiplyColor.mulColor = parseColor(entry)
		}
		if (entry.key == MULTIPLY_COLOR_ADD_COLOR) {
			ret.multiplyColor.addColor = parseColor(entry)
		}
	}

	private fun parseInt(entry: MutableMap.MutableEntry<Any, Any>): Int {
		if (entry.value.toString().isEmpty()) {
			throw IllegalArgumentException("Empty value for ${entry.key}")
		}
		try {
			return entry.value.toString().toInt()
		} catch (e: Throwable) {
			throw IllegalArgumentException("Failed to convert ${entry.key} = ${entry.value} to int", e)
		}
	}

	private fun parseDouble(entry: MutableMap.MutableEntry<Any, Any>): Double {
		if (entry.value.toString().isEmpty()) {
			throw IllegalArgumentException("Empty value for ${entry.key}")
		}
		try {
			return entry.value.toString().toDouble()
		} catch (e: Throwable) {
			throw IllegalArgumentException("Failed to convert ${entry.key} = ${entry.value} to double", e)
		}
	}

	private fun parseBool(entry: MutableMap.MutableEntry<Any, Any>): Boolean {
		return entry.value.toString().toBoolean()
	}

	private fun parseString(entry: MutableMap.MutableEntry<Any, Any>): String {
		return entry.value.toString()
	}

	private fun parseColor(entry: MutableMap.MutableEntry<Any, Any>): Color {
		return ColorUtils.parse(entry.value.toString())
	}

	private fun <E : Enum<E>> parseEnum(entry: MutableMap.MutableEntry<Any, Any>, enumClass: Class<E>): E {
		return java.lang.Enum.valueOf(enumClass, entry.value.toString())
	}
}
