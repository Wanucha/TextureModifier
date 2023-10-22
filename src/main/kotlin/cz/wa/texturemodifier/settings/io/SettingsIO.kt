package cz.wa.texturemodifier.settings.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import cz.wa.texturemodifier.settings.Settings
import org.yaml.snakeyaml.LoaderOptions
import java.io.File


object SettingsIO {

    private val config = LoaderOptions().apply {
        isEnumCaseSensitive = false
    }

    fun load(file: File): Settings {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readValue(file, Settings::class.java)
    }

    fun save(file: File, settings: Settings) {

    }
}
