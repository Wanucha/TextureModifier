package cz.wa.texturemodifier.settings.io

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import cz.wa.texturemodifier.math.ColorUtils
import java.awt.Color


class ColorDeserializer(vc: Class<*>?) : StdDeserializer<Color>(vc) {
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Color {
        val str = jp.valueAsString
        return ColorUtils.parse(str)
    }
}
