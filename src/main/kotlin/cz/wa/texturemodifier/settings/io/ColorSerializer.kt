package cz.wa.texturemodifier.settings.io

import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color

class ColorSerializer : ScalarSerializer<Color> {
	override fun write(value: Color?): String? {
		if (value == null) return null
		return ColorUtils.toString(value)
	}

	override fun read(value: String?): Color? {
		if (value == null) return null
		val hex = value.trim()
		require(hex.matches(Regex("^#?[0-9a-fA-F]{6}$"))) { "Invalid color format: $hex" }
		return ColorUtils.parse(hex)
	}
}
