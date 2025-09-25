package cz.wa.texturemodifier

import cz.wa.texturemodifier.gui.MainFrame
import cz.wa.texturemodifier.settings.Settings
import cz.wa.texturemodifier.settings.io.SettingsIO
import java.io.File

class TextureModifierMain {

	companion object {
		const val VERSION = "0.4.1"

		val IMAGE_OPEN_EXTS = arrayOf("png", "jpg", "jpeg", "gif", "bmp")
		val IMAGE_SAVE_EXTS = arrayOf("png", "gif", "bmp")
		val IMAGE_JPG_EXTS = arrayOf("jpg", "jpeg")

		private const val COMMAND_PREFIX = "--"

		@JvmStatic
		fun main(args: Array<String>) {
			println(printTitle())
			var settings = Settings()
			var files = emptyList<String>()
			var settingsFile: File? = null
			try {
				if (args.isNotEmpty()) {
					val settingsFileName = findSettings(args)
					if (settingsFileName.isNotEmpty()) {
						settingsFile = File(settingsFileName)
						settings = SettingsIO.load(settingsFile)
						if (SettingsIO.isProperties(settingsFile)) {
							println("Loaded obsolete properties file, save settings as yml and load the new file next time")
						}
					}
					files = parseImageFiles(args)
					val commands = parseCommands(args)

					if (commands.isNotEmpty()) {
						// has commands, run them
						if (settingsFileName.isEmpty()) {
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
			MainFrame(settings, settingsFile, files)
		}

		private fun findSettings(args: Array<String>): String {
			var ret = ""
			for (arg in args) {
				if (arg.endsWith(".yml", true) || arg.endsWith(".yaml", true)
					|| arg.endsWith(".properties", true)
				) {
					if (ret.isEmpty()) {
						ret = arg
					} else {
						println("Multiple settings, ignored: $arg")
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
			return args.filter { !it.startsWith(COMMAND_PREFIX) && IMAGE_OPEN_EXTS.contains(File(it).extension.lowercase()) }
		}

		fun printTitle(): String {
			return "Texture modifier v$VERSION\n" +
					"Created by Ondřej Milenovský\n" +
					"https://github.com/Wanucha/TextureModifier\n" +
					"----------------------------\n\n"
		}

		fun printUsage(): String {
			return "Usage:\n" +
					"file path as an argument - open the file at start (can be image or settings)\n" +
					"\tfile in a directory can be specified with * or ?, example 'img/*.png'\n" +
					"If no command specified, starts GUI\n" +
					"If a command is specified, processes the input files and saves them\n" +
					"There can be multiple commands, even one command multiple times\n" +
					"Command list:\n" +
					CommandLauncher.ALL_COMMANDS.joinToString("\n")
		}
	}
}
