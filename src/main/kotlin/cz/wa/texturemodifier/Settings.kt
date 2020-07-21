package cz.wa.texturemodifier

import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

class Settings(
    var file: File? = null,
    var guiBgColor: Color = Color.BLACK,
    var outPrefix: String = "",
    var outPostfix: String = "",
    var outFormat: String = "png",
    var seamlessDist: Int = 8,
    var seamlessAlpha: Boolean = false,
    var blurRadius: Double = 3.0,
    var blurRatio: Double = 0.5
) {
    companion object {
        const val GUI_BG_COLOR = "gui-bg-color"
        const val OUT_PREFIX = "out-prefix"
        const val OUT_POSTFIX = "out-postfix"
        const val OUT_FORMAT = "out-format"
        const val SEAMLESS_DIST = "seamless-dist"
        const val SEAMLESS_ALPHA = "seamless-alpha"
        const val BLUR_RADIUS = "blur-radius"
        const val BLUR_RATIO = "blur-ratio"

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
            }
            if (ret.outPrefix.isEmpty() && ret.outPostfix.isEmpty()) {
                throw IllegalArgumentException("$OUT_PREFIX and $OUT_POSTFIX must not be both empty")
            }
            return ret
        }

        fun save(s: Settings, file: File) {
            val sb = StringBuilder()

            write(sb, GUI_BG_COLOR, colorToString(s.guiBgColor))
            write(sb, OUT_PREFIX, s.outPrefix)
            write(sb, OUT_POSTFIX, s.outPostfix)
            write(sb, OUT_FORMAT, s.outFormat)
            write(sb, SEAMLESS_DIST, s.seamlessDist)
            write(sb, SEAMLESS_ALPHA, s.seamlessAlpha)
            write(sb, BLUR_RADIUS, s.blurRadius)
            write(sb, BLUR_RATIO, s.blurRatio)

            file.writeText(sb.toString())
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
            return Color(Integer.parseInt(entry.value.toString().replaceFirst("#", ""), 16))
        }

        private fun colorToString(c: Color): String {
            // TODO fix format when 0
            return "#${c.red.toString(16)}${c.green.toString(16)}${c.blue.toString(16)}"
        }
    }
}
