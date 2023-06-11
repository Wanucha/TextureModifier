package cz.wa.texturemodifier

import cz.wa.texturemodifier.gui.MainFrame
import java.io.File

class TextureModifierMain {

    companion object {
        const val VERSION = "0.2.1"
        val IMAGE_EXTS = arrayOf("png", "jpg", "jpeg", "gif", "bmp")

        @JvmStatic
        fun main(args: Array<String>) {
            var settings = Settings()
            var files = emptyList<String>()
            try {
                if (args.isNotEmpty()) {
                    val settingsFile = findProperties(args)
                    if (settingsFile.isNotEmpty()) {
                        settings = Settings.parseFile(settingsFile)
                    }
                    files = parseFiles(args)
                }
            } catch (e: Throwable) {
                println(printUsage())
                println()
                println("Error parsing arguments:")
                printMessages(e)
                return
            }
            MainFrame(settings, files)
        }

        private fun findProperties(args: Array<String>): String {
            var ret = ""
            for (arg in args) {
                if (arg.endsWith(".properties")) {
                    if (ret.isEmpty()) {
                        ret = arg
                    } else {
                        println("Multiple properties, ignored: $arg")
                    }
                }
            }
            return ret
        }

        private fun printMessages(e: Throwable) {
            var ex: Throwable? = e
            while (ex != null) {
                println("${ex.javaClass.simpleName}: ${ex.message}")
                ex = ex.cause
            }
        }

        private fun parseFiles(args: Array<String>): List<String> {
            return args.filter { IMAGE_EXTS.contains(File(it).extension) }
        }

        fun printTitle(): String {
            return "Texture modifier v$VERSION\n" +
                    "Created by Ondřej Milenovský\n" +
                    "----------------------------\n\n"
        }

        fun printUsage(): String {
            return "Usage:\n" +
                    "file path as an argument - open the file at start (can be image or properties)\n" +
                    "\tfile in a directory can be specified with * or ?, example 'img/*.png'\n" +
                    "If no command specified, starts GUI\n" +
                    "If a command is specified, processes the input files and saves them\n" +
                    "There can be multiple commands, even one command multiple times\n" +
                    "Command list:\n" +
                    "--seamless\n" +
                    "--blur\n" +
                    "--pixelate\n" +
                    "--fill_bg\n" +
                    "--merge_maps\n" +
                    "--multiply_color\n" +
                    "--remove_alpha\n"
        }
    }
}