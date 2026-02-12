package org.hwabeag.cashsystem

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.PluginCommand
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.hwabeag.cashsystem.commands.AmountCommand
import org.hwabeag.cashsystem.commands.CashCommand
import org.hwabeag.cashsystem.commands.ShopCommand
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.database.CashRepository
import org.hwabeag.cashsystem.database.DatabaseFactory
import org.hwabeag.cashsystem.events.InvClickEvent
import org.hwabeag.cashsystem.events.InvCloseEvent
import org.hwabeag.cashsystem.events.JoinEvent
import org.hwabeag.cashsystem.expansions.CashSystemExpansion

class CashSystem : JavaPlugin() {
    companion object {
        lateinit var instance: CashSystem
            private set

        lateinit var cashRepository: CashRepository
            private set
    }

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        ConfigManager.initialize(this)
        cashRepository = DatabaseFactory.create(this, ConfigManager.getConfig("setting"))

        registerCommands()
        registerEvents()
        migrateLegacyPlayerCash()

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            CashSystemExpansion(this).register()
        }
        logger.info("[CashSystem] Enable")
    }

    override fun onDisable() {
        ConfigManager.saveConfigs()
        cashRepository.close()
        logger.info("[CashSystem] Disable")
    }

    fun prefix(): String {
        val raw = ConfigManager.getConfig("cash-system").getString("cash-system.prefix") ?: "&a&l[CashSystem]&7"
        return ChatColor.translateAlternateColorCodes('&', raw)
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(InvClickEvent(this), this)
        server.pluginManager.registerEvents(InvCloseEvent(), this)
        server.pluginManager.registerEvents(JoinEvent(), this)
    }

    private fun registerCommands() {
        bindCommand("캐시", CashCommand(this))
        bindCommand("캐시상점", ShopCommand(this))
        bindCommand("금액", AmountCommand(this))
    }

    private fun bindCommand(name: String, executor: org.bukkit.command.TabExecutor) {
        val command: PluginCommand = requireNotNull(getCommand(name)) {
            "plugin.yml 에 '$name' 명령어가 등록되어 있지 않습니다."
        }
        command.setExecutor(executor)
        command.tabCompleter = executor
    }

    private fun migrateLegacyPlayerCash() {
        val settingConfig: FileConfiguration = ConfigManager.getConfig("setting")
        if (!settingConfig.getBoolean("database.migrate-player-yml", true)) {
            return
        }
        val playerConfig = ConfigManager.getConfig("player")
        for (playerName in playerConfig.getKeys(false)) {
            val legacyCash = playerConfig.getInt("$playerName.캐시", Int.MIN_VALUE)
            if (legacyCash == Int.MIN_VALUE) {
                continue
            }
            if (cashRepository.getCash(playerName) == 0L && legacyCash > 0) {
                cashRepository.setCash(playerName, legacyCash.toLong())
            }
        }
        settingConfig.set("database.migrate-player-yml", false)
        ConfigManager.saveConfigs()
    }
}

