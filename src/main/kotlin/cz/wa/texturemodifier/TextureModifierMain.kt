package cz.wa.texturemodifier

import cz.wa.texturemodifier.gui.MainFrame
import java.io.File

class TextureModifierMain {

    companion object {
        const val VERSION = "0.3.1"

        val IMAGE_EXTS = arrayOf("png", "jpg", "jpeg", "gif", "bmp")

        private const val COMMAND_PREFIX = "--"

        @JvmStatic
        fun main(args: Array<String>) {
            println(printTitle())
            var settings = Settings()
            var files = emptyList<String>()
            try {
                if (args.isNotEmpty()) {
                    val settingsFile = findProperties(args)
                    if (settingsFile.isNotEmpty()) {
                        settings = Settings.parseFile(settingsFile)
                    }
                    files = parseImageFiles(args)
                    val commands = parseCommands(args)

                    if (commands.isNotEmpty()) {
                        // has commands, run them
                        if (settingsFile.isEmpty()) {
                            println("Error running commands: Commands found but no settings specified")
                        } else {
                            println("Commands found (${commands.size}), running commands")
                            CommandLauncher(settings, files, commands).execute()
                        }
                        return
                    }
                }
            } catch (e: Throwable) {
                println(printUsage())
                println()
                println("Error parsing arguments:")
                printMessages(e)
                throw e
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

        private fun parseCommands(args: Array<String>): List<String> {
            return args.filter { it.startsWith(COMMAND_PREFIX) }
        }

        private fun parseImageFiles(args: Array<String>): List<String> {
            return args.filter { !it.startsWith(COMMAND_PREFIX) && IMAGE_EXTS.contains(File(it).extension) }
        }

        fun printTitle(): String {
            return "Texture modifier v$VERSION\n" +
                    "Created by Ondřej Milenovský\n" +
                    "https://github.com/Wanucha/TextureModifier\n" +
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
                    "${CommandLauncher.ALL_COMMANDS.joinToString("\n")}"
        }
    }
}