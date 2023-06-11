package cz.wa.texturemodifier

import org.apache.commons.io.filefilter.WildcardFileFilter
import java.io.File
import java.io.FilenameFilter
import java.nio.file.Paths


/**
 * Runs the commands
 */
class CommandLauncher(
    private val settings: Settings,
    private val files: List<String>,
    private val commands: List<String>
) {

    fun execute() {
        if (!validateInput()) {
            throw IllegalArgumentException("Error validating input")
        }
        val imageFiles = parseFiles()
        println("Commands to run:\n${commands.joinToString("\n")}")
        println("Found ${imageFiles.size} image files to process")

        for ((i, imageFile) in imageFiles.withIndex()) {
            println("${i + 1}/${imageFiles.size} ${imageFile.path}")
        }
    }

    private fun validateInput(): Boolean {
        var valid = true
        for (command in commands) {
            if (!ALL_COMMANDS.contains(command)) {
                valid = false
                println("Invalid command '${command}'")
            }
        }
        return valid
    }

    private fun parseFiles(): List<File> {
        val ret = ArrayList<File>()

        for (file in files) {
            if (file.contains('?') || file.contains('*')) {
                listFiles(ret, file)
            } else {
                ret.add(File(file))
            }
        }

        return ret;
    }

    private fun listFiles(ret: ArrayList<File>, file: String) {
        val file = file.replace('\\', '/')
        val ind = file.lastIndexOf('/')

        val dir = if (ind >= 0) File(file.substring(0, ind)) else Paths.get("").toFile()
        val fileName = if (ind >= 0) file.substring(ind + 1) else file

        ret.addAll(dir.listFiles(WildcardFileFilter.builder().setWildcards(fileName).get() as FilenameFilter))
    }

    companion object {
        private const val SEAMLESS_CMD = "--seamless"
        private const val BLUR_CMD = "--blur"
        private const val PIXELATE_CMD = "--pixelate"
        private const val FILL_BG_CMD = "--fill_bg"
        private const val MERGE_MAPS_CMD = "--merge_maps"
        private const val MULTIPLY_COLOR_CMD = "--multiply_color"
        private const val REMOVE_ALPHA_CMD = "--remove_alpha"

        val ALL_COMMANDS = arrayOf(
            SEAMLESS_CMD,
            BLUR_CMD,
            PIXELATE_CMD,
            FILL_BG_CMD,
            MERGE_MAPS_CMD,
            MULTIPLY_COLOR_CMD,
            REMOVE_ALPHA_CMD
        )
    }
}