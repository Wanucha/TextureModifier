package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.MapType
import cz.wa.texturemodifier.Settings
import cz.wa.texturemodifier.gui.utils.ImageUtils
import cz.wa.texturemodifier.image.Texture
import cz.wa.texturemodifier.math.ColorUtils
import cz.wa.texturemodifier.math.Vec2i
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

/**
 * Merges multiple maps into one.
 * The input image consist of multiple images side by side. For each image there is transform like:
 * map1: RGB-R
 * map2: G-AB
 * That means average values RGB from left and convert to R.
 * Then take G from right and convert to alpha and blue (same value for both).
 */
class MergeMapCommand(settings: Settings) : AbstractCommand(settings) {

    override fun execute(image: BufferedImage): BufferedImage {
        val maps = ArrayList<MapConvert>(4)
        maps.add(parseMap(settings.mergeMapsMap1))
        maps.add(parseMap(settings.mergeMapsMap2))
        if (settings.mergeMapsLayout == MapType.FOUR_SQUARE) {
            maps.add(parseMap(settings.mergeMapsMap3))
            maps.add(parseMap(settings.mergeMapsMap4))
        }

        check(settings.mergeMapsLayout == MapType.TWO_ABOVE || image.width % 2 == 0) { "Layout has maps by side, image width must be even" }
        check(settings.mergeMapsLayout == MapType.TWO_SIDE || image.height % 2 == 0) { "Layout has maps above, image height must be even" }

        val w = if (settings.mergeMapsLayout == MapType.TWO_ABOVE) image.width else image.width / 2
        val h = if (settings.mergeMapsLayout == MapType.TWO_SIDE) image.height else image.height / 2

        val ret = ImageUtils.createEmptyImage(w, h)
        val inTex = Texture(image)
        val outTex = Texture(ret)

        for (y in 0 until h) {
            for (x in 0 until w) {
                convertPixel(inTex, x, y, outTex, maps)
            }
        }
        return ret
    }

    override fun getHelp(): String = "Converts multiple parts of the input into specified channels\n" +
            "The input consist of multiple variants of a single texture, e. g.:\n" +
            "Size is 32x16, left image is 16x16 metallic map, right image is 16x16 smoothness map.\n" +
            "Then it converts each map into single 16x16 image, where each channel is generated from different input map.\n" +
            "* input layout:\n" +
            "\t- TWO_SIDE: 2 images side by side (1 left, 2 right)\n" +
            "\t- TWO_ABOVE: 2 images first on top, second below (1 top, 2 bottom)\n" +
            "\t- FOUR_SQUARE: 4 images in 2x2 grid (1 upper left, 2 upper right, 3 lower left, 4 lower right)\n" +
            "* map [1-4] - defines conversion for each input map, format is 'input-output'\n" +
            "\tinput and output must consists only from characters R, G, B, A\n" +
            "\tconversion takes all input channels, averages them and fills each output channel\n" +
            "\texample1: RGB-R take gray value and fill red channel\n" +
            "\texample2: RB-AG take average of red and blue and fill green and alpha channels with the value"

    private fun convertPixel(inTex: Texture, x: Int, y: Int, outTex: Texture, maps: ArrayList<MapConvert>) {
        val output = ArrayList<Int>(4)
        val c1 = getInputPixel(inTex, x, y, Vec2i.ZERO)
        output.add(maps[0].convert(c1))
        if (settings.mergeMapsLayout == MapType.TWO_SIDE || settings.mergeMapsLayout == MapType.FOUR_SQUARE) {
            val c2 = getInputPixel(inTex, x, y, Vec2i(1, 0))
            output.add(maps[1].convert(c2))
        } else {
            val c2 = getInputPixel(inTex, x, y, Vec2i(0, 1))
            output.add(maps[1].convert(c2))
        }

        if (settings.mergeMapsLayout == MapType.FOUR_SQUARE) {
            val c3 = getInputPixel(inTex, x, y, Vec2i(0, 1))
            output.add(maps[2].convert(c3))
            val c4 = getInputPixel(inTex, x, y, Vec2i(1, 1))
            output.add(maps[3].convert(c4))
        }

        outTex.setPoint(x, y, mergeColors(output, maps))
    }

    private fun mergeColors(colors: ArrayList<Int>, maps: ArrayList<MergeMapCommand.MapConvert>): Int {
        val r = ArrayList<Int>(4)
        val g = ArrayList<Int>(4)
        val b = ArrayList<Int>(4)
        val a = ArrayList<Int>(4)
        var i = 0
        for (color in colors) {
            if (maps[i].output.r) {
                r.add(ColorUtils.getRed(color))
            }
            if (maps[i].output.g) {
                g.add(ColorUtils.getGreen(color))
            }
            if (maps[i].output.b) {
                b.add(ColorUtils.getBlue(color))
            }
            if (maps[i].output.a) {
                a.add(ColorUtils.getAlpha(color))
            }
            i++
        }
        return ColorUtils.fromRGBA(averageColor(r), averageColor(g), averageColor(b), averageColor(a))
    }

    private fun averageColor(c: ArrayList<Int>): Int {
        if (c.isEmpty()) {
            return 0
        } else {
            return c.average().roundToInt()
        }
    }

    private fun getInputPixel(inTex: Texture, x: Int, y: Int, map: Vec2i): Int =
        inTex.getPoint(x + map.x * inTex.width / 2, y + map.y * inTex.height / 2)

    private fun parseMap(map: String): MapConvert {
        val str = trim(map)
        val ind = map.indexOf('-')
        check(ind >= 0) { "Missing separator '-' in: $map" }
        val input = parseChannels(str.substring(0, ind))
        val output = parseChannels(str.substring(ind + 1))
        check(!input.isEmpty()) { "Empty input in: $map" }
        check(!output.isEmpty()) { "Empty output in: $map" }
        return MapConvert(input, output)
    }

    private fun parseChannels(str: String): MapChannels {
        var r = false
        var g = false
        var b = false
        var a = false
        for (c in str) {
            if (c == 'r' || c == 'R') {
                r = true
            } else if (c == 'g' || c == 'G') {
                g = true
            } else if (c == 'b' || c == 'B') {
                b = true
            } else if (c == 'a' || c == 'A') {
                a = true
            } else {
                throw java.lang.IllegalArgumentException("Unexpected character '$c' in $str")
            }
        }
        return MapChannels(r, g, b, a)
    }

    private fun trim(str: String) = str.replace("\\s", "")

    private class MapChannels(val r: Boolean, val g: Boolean, val b: Boolean, val a: Boolean) {
        fun isEmpty() = !r && !g && !b && !a

        /** Returns single byte average of selected channels */
        fun getValue(color: Int): Int {
            var sum = 0
            var count = 0
            if (r) {
                sum += ColorUtils.getRed(color)
                count++
            }
            if (g) {
                sum += ColorUtils.getGreen(color)
                count++
            }
            if (b) {
                sum += ColorUtils.getBlue(color)
                count++
            }
            if (a) {
                sum += ColorUtils.getAlpha(color)
                count++
            }
            return (sum / count.toDouble()).roundToInt()
        }

        /** Returns color with equal channels by selected */
        fun getColor(value: Int): Int {
            val cr = if (r) value else 0
            val cg = if (g) value else 0
            val cb = if (b) value else 0
            val ca = if (a) value else 0
            return ColorUtils.fromRGBA(cr, cg, cb, ca)
        }
    }

    private class MapConvert(val input: MapChannels, var output: MapChannels) {
        fun convert(color: Int) = output.getColor(input.getValue(color))
    }
}