package cz.wa.texturemodifier.settings

import java.io.File

class Settings(
    var file: File? = null,
    var out: OutSettings = OutSettings(),
    var gui: GuiSettings = GuiSettings(),
    var seamless: SeamlessSettings = SeamlessSettings(),
    var blur: BlurSettings = BlurSettings(),
    var pixelate: PixelateSettings = PixelateSettings(),
    var fillBg: FillBgSettings = FillBgSettings(),
    var mergeMaps: MergeMapsSettings = MergeMapsSettings(),
    var multiplyColor: MultiplyColorSettings = MultiplyColorSettings(),
    var removeAlpha: RemoveAlphaSettings = RemoveAlphaSettings(),
)
