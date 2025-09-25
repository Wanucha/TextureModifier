package cz.wa.texturemodifier.settings

data class MergeMapsSettings(
	var layout: MapType = MapType.TWO_SIDE,
	var map1: String = "RGB-R",
	var map2: String = "RGB-A",
	var map3: String = "RGB-G",
	var map4: String = "RGB-B",
)
