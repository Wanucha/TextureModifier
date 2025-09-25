package cz.wa.texturemodifier.settings

data class Settings(
	var output: OutputSettings = OutputSettings(),
	var gui: GuiSettings = GuiSettings(),
	var seamless: SeamlessSettings = SeamlessSettings(),
	var blur: BlurSettings = BlurSettings(),
	var pixelate: PixelateSettings = PixelateSettings(),
	var fillBackground: FillBackgroundSettings = FillBackgroundSettings(),
	var mergeMaps: MergeMapsSettings = MergeMapsSettings(),
	var multiplyColor: MultiplyColorSettings = MultiplyColorSettings(),
	var removeAlpha: RemoveAlphaSettings = RemoveAlphaSettings(),
)
