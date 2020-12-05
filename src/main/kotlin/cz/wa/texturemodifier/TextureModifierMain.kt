package cz.wa.texturemodifier

import cz.wa.texturemodifier.gui.MainFrame
import java.io.File

class TextureModifierMain {

    companion object {
        const val VERSION = "0.1"
        val IMAGE_EXTS = arrayOf("png", "jpg", "jpeg", "gif", "bmp")

        @JvmStatic
        fun main(args: Array<String>) {
            var settings = Settings()
            var files = emptyList<String>()
            try {
                if (args.size > 0) {
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
                        println("Multiple properties, ignored: " + arg)
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
            return args.filter { TextureModifierMain.IMAGE_EXTS.contains(File(it).extension) }
        }

        fun printTitle(): String {
            return "Texture modifier v$VERSION\n" +
                    "Created by Ondřej Milenovský\n" +
                    "----------------------------\n\n"
        }

        fun printUsage(): String {
            return "Usage:???"
        }
    }
}