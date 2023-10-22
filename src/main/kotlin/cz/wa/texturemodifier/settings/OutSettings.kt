package cz.wa.texturemodifier.settings

import cz.wa.texturemodifier.OverwriteType

class OutSettings (
    var filePrefix: String = "output/",
    var filePostfix: String = "",
    var fileFormat: String = "",
    var overwriteType: OverwriteType = OverwriteType.IGNORE,
)
