package cz.wa.texturemodifier

import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

class Settings(
    var file: File? = null,
    var guiBgColor: Color = Color.BLACK,
    var guiShowBounds: Boolean = true,
    var outPrefix: String = "",
    var outPostfix: String = "",
    var outFormat: String = "png",
    var seamlessDist: Int = 8,
    var seamlessAlpha: Boolean = false,
    var blurRadius: Double = 3.0,
    var blurRatio: Double = 0.5,
    var pixelateScale: Double = 4.0,
    var pixelateSizeX: Int = 0,
    var pixelateSizeY: Int = 0,
    var pixelateColors: Int = 12,
    var pixelateScaleType: ScaleType = ScaleType.NEAREST,
    var pixelateScaleColorTolerance: Int = 5,
    var pixelateIgnoreBgColor: Boolean = false,
    var pixelateBgColor: Color = Color.BLACK,
    var fillBgIterations: Int = 1,
    var fillBgIncludeCorners: Boolean = false,
    var fillBgAverageFill: Boolean = true,
    var fillBgBgColor: Color = Color.BLACK,
    var mergeMapsLayout: MapType = MapType.TWO_SIDE,
    var mergeMapsMap1: String = "RGB-R",
    var mergeMapsMap2: String = "RGB-A",
    var mergeMapsMap3: String = "RGB-G",
    var mergeMapsMap4: String = "RGB-B",
    var removeAlphaThreshhold: Int = 128,
) {
    companion object {
        const val GUI_BG_COLOR = "gui-bg-color"
        const val GUI_SHOW_BOUNDS = "gui-show_bounds"
        const val OUT_PREFIX = "out-prefix"
        const val OUT_POSTFIX = "out-postfix"
        const val OUT_FORMAT = "out-format"
        const val SEAMLESS_DIST = "seamless-dist"
        const val SEAMLESS_ALPHA = "seamless-alpha"
        const val BLUR_RADIUS = "blur-radius"
        const val BLUR_RATIO = "blur-ratio"
        const val PIXELATE_SCALE = "pixelate-scale"
        const val PIXELATE_COLORS = "pixelate-colors"
        const val PIXELATE_SCALE_TYPE = "pixelate-scale-type"
        const val PIXELATE_SCALE_COLOR_TOLERANCE = "pixelate-scale-color-tolerance"
        const val PIXELATE_IGNORE_BG_COLOR = "pixelate-ignore-bg-color"
        const val PIXELATE_BG_COLOR = "pixelate-bg-color"
        const val FILL_BG_ITERATIONS = "fillbg-iterations"
        const val FILL_BG_INCLUDE_CORNERS = "fillbg-include-corners"
        const val FILL_BG_AVERAGE_FILL = "fillbg-average-fill"
        const val FILL_BG_BG_COLOR = "fillbg-bg-color"
        const val MERGE_MAPS_LAYOUT = "merge-maps-layout"
        const val MERGE_MAPS_MAP1 = "merge-maps-map1"
        const val MERGE_MAPS_MAP2 = "merge-maps-map2"
        const val MERGE_MAPS_MAP3 = "merge-maps-map3"
        const val MERGE_MAPS_MAP4 = "merge-maps-map4"
        const val REMOVE_ALPHA_THRESHHOLD = "remove-alpha-threshhold"

        fun parseFile(fileName: String): Settings {
            val ret = parseString(File(fileName).readText())
            ret.file = File(fileName)
            return ret
        }

        fun parseString(text: String): Settings {
            val p = Properties()
            p.load(ByteArrayInputStream(text.toByteArray(Charsets.UTF_8)))
            val ret = Settings()
            for (entry in p) {
                if (entry.key == GUI_BG_COLOR) {
                    ret.guiBgColor = parseColor(entry)
                }
                if (entry.key == GUI_SHOW_BOUNDS) {
                    ret.guiShowBounds = parseBool(entry)
                }
                if (entry.key == OUT_PREFIX) {
                    ret.outPrefix = parseString(entry)
                }
                if (entry.key == OUT_POSTFIX) {
                    ret.outPostfix = parseString(entry)
                }
                if (entry.key == OUT_FORMAT) {
                    ret.outFormat = parseString(entry)
                }
                if (entry.key == SEAMLESS_DIST) {
                    ret.seamlessDist = parseInt(entry)
                }
                if (entry.key == SEAMLESS_ALPHA) {
                    ret.seamlessAlpha = parseBool(entry)
                }
                if (entry.key == BLUR_RADIUS) {
                    ret.blurRadius = parseDouble(entry)
                }
                if (entry.key == BLUR_RATIO) {
                    ret.blurRatio = parseDouble(entry)
                }
                if (entry.key == PIXELATE_SCALE) {
                    ret.pixelateScale = parseDouble(entry)
                }
                if (entry.key == PIXELATE_COLORS) {
                    ret.pixelateColors = parseInt(entry)
                }
                if (entry.key == PIXELATE_SCALE_TYPE) {
                    ret.pixelateScaleType = parseEnum(entry, ScaleType::class.java)
                }
                if (entry.key == PIXELATE_SCALE_COLOR_TOLERANCE) {
                    ret.pixelateScaleColorTolerance = parseInt(entry)
                }
                if (entry.key == PIXELATE_IGNORE_BG_COLOR) {
                    ret.pixelateIgnoreBgColor= parseBool(entry)
                }
                if (entry.key == PIXELATE_BG_COLOR) {
                    ret.pixelateBgColor = parseColor(entry)
                }
                if (entry.key == FILL_BG_ITERATIONS) {
                    ret.fillBgIterations = parseInt(entry)
                }
                if (entry.key == FILL_BG_INCLUDE_CORNERS) {
                    ret.fillBgIncludeCorners = parseBool(entry)
                }
                if (entry.key == FILL_BG_AVERAGE_FILL) {
                    ret.fillBgAverageFill = parseBool(entry)
                }
                if (entry.key == FILL_BG_BG_COLOR) {
                    ret.fillBgBgColor = parseColor(entry)
                }
                if (entry.key == MERGE_MAPS_LAYOUT) {
                    ret.mergeMapsLayout = parseEnum(entry, MapType::class.java)
                }
                if (entry.key == MERGE_MAPS_MAP1) {
                    ret.mergeMapsMap1 = parseString(entry)
                }
                if (entry.key == MERGE_MAPS_MAP2) {
                    ret.mergeMapsMap2 = parseString(entry)
                }
                if (entry.key == MERGE_MAPS_MAP3) {
                    ret.mergeMapsMap3 = parseString(entry)
                }
                if (entry.key == MERGE_MAPS_MAP4) {
                    ret.mergeMapsMap4 = parseString(entry)
                }
                if (entry.key == REMOVE_ALPHA_THRESHHOLD) {
                    ret.removeAlphaThreshhold = parseInt(entry)
                }
            }
            if (ret.outPrefix.isEmpty() && ret.outPostfix.isEmpty()) {
                throw IllegalArgumentException("$OUT_PREFIX and $OUT_POSTFIX must not be both empty")
            }
            return ret
        }

        fun save(s: Settings, file: File) {
            file.writeText(generateText(s))
        }

        fun generateText(s: Settings): String {
            val sb = StringBuilder()

            write(sb, GUI_BG_COLOR, ColorUtils.toString(s.guiBgColor))
            write(sb, GUI_SHOW_BOUNDS, s.guiShowBounds)
            write(sb, OUT_PREFIX, s.outPrefix)
            write(sb, OUT_POSTFIX, s.outPostfix)
            write(sb, OUT_FORMAT, s.outFormat)
            write(sb, SEAMLESS_DIST, s.seamlessDist)
            write(sb, SEAMLESS_ALPHA, s.seamlessAlpha)
            write(sb, BLUR_RADIUS, s.blurRadius)
            write(sb, BLUR_RATIO, s.blurRatio)
            write(sb, PIXELATE_SCALE, s.pixelateScale)
            write(sb, PIXELATE_COLORS, s.pixelateColors)
            write(sb, PIXELATE_SCALE_TYPE, s.pixelateScaleType)
            write(sb, PIXELATE_SCALE_COLOR_TOLERANCE, s.pixelateScaleColorTolerance)
            write(sb, PIXELATE_IGNORE_BG_COLOR, s.pixelateIgnoreBgColor)
            write(sb, PIXELATE_BG_COLOR, ColorUtils.toString(s.pixelateBgColor))
            write(sb, FILL_BG_ITERATIONS, s.fillBgIterations)
            write(sb, FILL_BG_INCLUDE_CORNERS, s.fillBgIncludeCorners)
            write(sb, FILL_BG_AVERAGE_FILL, s.fillBgAverageFill)
            write(sb, FILL_BG_BG_COLOR, ColorUtils.toString(s.fillBgBgColor))
            write(sb, MERGE_MAPS_LAYOUT, s.mergeMapsLayout)
            write(sb, MERGE_MAPS_MAP1, s.mergeMapsMap1)
            write(sb, MERGE_MAPS_MAP2, s.mergeMapsMap2)
            write(sb, MERGE_MAPS_MAP3, s.mergeMapsMap3)
            write(sb, MERGE_MAPS_MAP4, s.mergeMapsMap4)
            write(sb, REMOVE_ALPHA_THRESHHOLD, s.removeAlphaThreshhold)
            return sb.toString()
        }

        fun write(sb: StringBuilder, key: String, value: Any) {
            sb.append(key).append("=").append(value).append("\n")
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

        private fun <E: Enum<E>> parseEnum(entry: MutableMap.MutableEntry<Any, Any>, enumClass: Class<E>): E {
            return java.lang.Enum.valueOf(enumClass, entry.value.toString());
        }
    }
}
