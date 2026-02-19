package org.hwabeag.cashsystem.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class ConfigMaker(plugin: JavaPlugin, path: String, fileName: String) {
    private val file: File = File(path, fileName)
    private val resourceExists: Boolean = plugin.getResource(fileName) != null
    private var config: FileConfiguration

    init {
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            if (resourceExists) {
                plugin.saveResource(fileName, false)
            } else {
                file.createNewFile()
            }
        }

        config = YamlConfiguration.loadConfiguration(file)
        applyResourceDefaults(plugin, fileName)
    }

    fun getConfig(): FileConfiguration = config

    fun saveConfig() {
        config.save(file)
    }

    fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file)
    }

    private fun applyResourceDefaults(plugin: JavaPlugin, fileName: String) {
        if (!resourceExists) {
            return
        }

        plugin.getResource(fileName)?.use { input ->
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            val defaults = YamlConfiguration.loadConfiguration(reader)
            config.setDefaults(defaults)
            config.options().copyDefaults(true)
        }
    }
}
