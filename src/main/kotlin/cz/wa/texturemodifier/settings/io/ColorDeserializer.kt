package cz.wa.texturemodifier.settings.io

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color

class ColorDeserializer : JsonDeserializer<Color>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Color {
        val hex = p.text.trim()
        require(hex.matches(Regex("^#?[0-9a-fA-F]{6}$"))) { "Invalid color format: $hex" }
        return ColorUtils.parse (hex)
    }
}
