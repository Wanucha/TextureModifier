package cz.wa.texturemodifier.settings

import java.awt.Color

class FillBgSettings(
    var bgIterations: Int = 1,
    var bgIncludeCorners: Boolean = false,
    var bgAverageFill: Boolean = true,
    //@JsonDeserialize(using = ColorDeserializer.class)
    var bgBgColor: Color = Color.BLACK,
)
