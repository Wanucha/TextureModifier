package cz.wa.texturemodifier.settings.io

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color

class ColorSerializer : JsonSerializer<Color>() {
    override fun serialize(value: Color, gen: JsonGenerator, serializers: SerializerProvider) {
        val hex = ColorUtils.toString(value)
        gen.writeString(hex)
    }
}
