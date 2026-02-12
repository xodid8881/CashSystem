package org.hwabeag.cashsystem.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigMaker(path: String, fileName: String) {
    private val file: File = File(path, fileName)
    private var config: FileConfiguration

    init {
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun getConfig(): FileConfiguration = config

    fun saveConfig() {
        config.save(file)
    }

    fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file)
    }
}

