package cz.wa.texturemodifier.settings

import java.awt.Color

data class FillBackgroundSettings (
    var iterations: Int = 1,
    var includeCorners: Boolean = false,
    var averageFill: Boolean = true,
    var bgColor: Color = Color.BLACK,
)
