package cz.wa.texturemodifier

import cz.wa.texturemodifier.gui.MainFrame

class TextureModifierMain {

    companion object {
        const val VERSION = "0.1"

        @JvmStatic
        fun main(args: Array<String>) {
            var settings = Settings()
            var files = emptyList<String>()
            try {
                if (args.size > 0) {
                    settings = Settings.parseFile(args[0])
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

        private fun printMessages(e: Throwable) {
            var ex: Throwable? = e
            while (ex != null) {
                println("${ex.javaClass.simpleName}: ${ex.message}")
                ex = ex.cause
            }
        }

        private fun parseFiles(args: Array<String>): List<String> {
            return args.toList().subList(1, args.size)
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