package cz.wa.texturemodifier.settings.io

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import cz.wa.texturemodifier.settings.Settings
import java.awt.Color
import java.io.File

/**
 * Class to load/save settings in YML format
 * Also supports reading legacy properties
 */
object SettingsIO {
    const val PROPERTIES_EXT = "properties"
    const val YML_EXT = "yml"
    const val YAML_EXT = "yaml"

    fun load(file: File): Settings {
        if (isProperties(file)) {
            return PropertiesSettingsParser.parseFile(file)
        }
        if (isYml(file)) {
            return mapper.readValue(file)
        }
        throw IllegalArgumentException("Unsupported format: " + file.extension)
    }

    fun save(file: File, settings: Settings) {
        if (isProperties(file)) {
            throw IllegalArgumentException("Settings cannot be saved as obsolete properties, use yml instead")
        }
        if (isYml(file)) {
            mapper.writeValue(file, settings)
            return
        }
        throw IllegalArgumentException("Unsupported format: " + file.extension)
    }

    fun loadFromString(ymlContent: String): Settings {
        return mapper.readValue(ymlContent)
    }

    fun saveToString(settings: Settings): String {
        return mapper.writeValueAsString(settings)
    }

    fun isYml(file: File): Boolean =
        file.extension.equals(YML_EXT, true) || file.extension.equals(YAML_EXT, true)

    fun isProperties(file: File): Boolean =
        file.extension.equals(PROPERTIES_EXT, true)

    private val mapper = YAMLMapper().apply {
        registerKotlinModule()
        registerModule(SimpleModule().apply {
            addDeserializer(Color::class.java, ColorDeserializer())
            addSerializer(Color::class.java, ColorSerializer())
        })
    }
}
