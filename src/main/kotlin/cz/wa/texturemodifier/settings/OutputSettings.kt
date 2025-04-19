package cz.wa.texturemodifier.settings

data class OutputSettings(
    var prefix: String = "output/",
    var postfix: String = "",
    var format: String = "",
    var overwriteType: OverwriteType = OverwriteType.IGNORE,
)
