package cz.wa.texturemodifier.settings.io

import com.esotericsoftware.yamlbeans.YamlConfig
import com.esotericsoftware.yamlbeans.YamlReader
import com.esotericsoftware.yamlbeans.YamlWriter
import cz.wa.texturemodifier.settings.Settings
import java.awt.Color
import java.io.*

/**
 * Class to load/save settings in YML format
 * Also supports reading legacy properties
 */
object SettingsIO {
    private const val PROPERTIES_EXT = "properties"
    private const val YML_EXT = "yml"
    private const val YAML_EXT = "yaml"

    private val config: YamlConfig = YamlConfig().apply {
        setClassTag("settings", Settings::class.java)
        setClassTag("color", Color::class.java)
        writeConfig.setWriteDefaultValues(true)
        writeConfig.setKeepBeanPropertyOrder(true)
        writeConfig.setIndentSize(2)
        writeConfig.setQuoteChar(YamlConfig.Quote.DOUBLE)

        // custom serializer
        setScalarSerializer(Color::class.java, ColorSerializer())
    }

    fun load(file: File): Settings {
        if (isProperties(file)) {
            return PropertiesSettingsParser.parseFile(file)
        }
        if (isYml(file)) {
            val reader = YamlReader(FileReader(file), config)
            return reader.read(Settings::class.java)
        }
        throw IllegalArgumentException("Unsupported format: ${file.extension}")
    }

    fun save(file: File, settings: Settings) {
        if (isProperties(file)) {
            throw IllegalArgumentException("Settings cannot be saved as obsolete properties, use yml instead")
        }
        if (isYml(file)) {
            val writer = YamlWriter(FileWriter(file), config)
            writer.write(settings)
            writer.close()
            return
        }
        throw IllegalArgumentException("Unsupported format: ${file.extension}")
    }

    fun loadFromString(ymlContent: String): Settings {
        val reader = YamlReader(StringReader(ymlContent), config)
        return reader.read(Settings::class.java)
    }

    fun saveToString(settings: Settings): String {
        val writer = StringWriter()
        val yamlWriter = YamlWriter(writer, config)
        yamlWriter.write(settings)
        yamlWriter.close()
        return writer.toString()
    }

    fun isYml(file: File): Boolean =
        file.extension.equals(YML_EXT, true) || file.extension.equals(YAML_EXT, true)

    fun isProperties(file: File): Boolean =
        file.extension.equals(PROPERTIES_EXT, true)
}
