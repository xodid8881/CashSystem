package org.hwabeag.cashsystem.config

import org.bukkit.configuration.file.FileConfiguration
import org.hwabeag.cashsystem.CashSystem

object ConfigManager {
    private lateinit var plugin: CashSystem
    private val configSet: MutableMap<String, ConfigMaker> = linkedMapOf()

    fun initialize(cashSystem: CashSystem) {
        plugin = cashSystem
        val path = plugin.dataFolder.absolutePath

        configSet["cash-system"] = ConfigMaker(path, "CashSystem.yml")
        configSet["setting"] = ConfigMaker(path, "config.yml")
        configSet["player"] = ConfigMaker(path, "Player.yml")

        loadDefaults()
        saveConfigs()
    }

    fun reloadConfigs() {
        configSet.values.forEach { it.reloadConfig() }
    }

    fun saveConfigs() {
        configSet.values.forEach { it.saveConfig() }
    }

    fun getConfig(fileName: String): FileConfiguration {
        return requireNotNull(configSet[fileName]) { "설정 파일 키를 찾을 수 없습니다: $fileName" }.getConfig()
    }

    private fun loadDefaults() {
        val cashSystemConfig = getConfig("cash-system")
        cashSystemConfig.options().copyDefaults(true)
        cashSystemConfig.addDefault("cash-system.prefix", "&a&l[CashSystem]&7")

        val settingConfig = getConfig("setting")
        settingConfig.options().copyDefaults(true)
        settingConfig.addDefault("database.type", "sqlite")
        settingConfig.addDefault("database.sqlite.file", "cashsystem.db")
        settingConfig.addDefault("database.mysql.host", "127.0.0.1")
        settingConfig.addDefault("database.mysql.port", 3306)
        settingConfig.addDefault("database.mysql.database", "cashsystem")
        settingConfig.addDefault("database.mysql.username", "root")
        settingConfig.addDefault("database.mysql.password", "change-me")
        settingConfig.addDefault("database.mysql.use-ssl", false)
        settingConfig.addDefault("database.migrate-player-yml", true)
        settingConfig.addDefault("shop-item-lore", listOf("&f&l- 현재 구매가 %amount%원", "&f&l- 클릭시 구매됩니다."))
    }
}

