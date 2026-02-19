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
    private var repositoryInitialized = false

    companion object {
        private const val COMMAND_CASH = "캐시"
        private const val COMMAND_SHOP = "캐시상점"
        private const val COMMAND_AMOUNT = "금액"

        private const val LEGACY_PLAYER_CASH_KEY = "캐시"

        lateinit var instance: CashSystem
            private set

        lateinit var cashRepository: CashRepository
            private set
    }

    override fun onEnable() {
        instance = this

        try {
            saveDefaultConfig()
            ConfigManager.initialize(this)

            cashRepository = DatabaseFactory.create(this, ConfigManager.getConfig("setting"))
            repositoryInitialized = true

            registerCommands()
            registerEvents()
            migrateLegacyPlayerCash()

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                CashSystemExpansion(this).register()
            }
        } catch (exception: Exception) {
            logger.severe("CashSystem 활성화 실패: ${exception.message}")
            logger.severe("불안정한 상태 방지를 위해 플러그인을 비활성화합니다.")
            server.pluginManager.disablePlugin(this)
            return
        }

        logger.info("[CashSystem] Enable")
    }

    override fun onDisable() {
        ConfigManager.saveConfigs()
        if (repositoryInitialized) {
            cashRepository.close()
        }
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
        bindCommand(COMMAND_CASH, CashCommand(this))
        bindCommand(COMMAND_SHOP, ShopCommand(this))
        bindCommand(COMMAND_AMOUNT, AmountCommand(this))
    }

    private fun bindCommand(name: String, executor: org.bukkit.command.TabExecutor) {
        val command: PluginCommand = requireNotNull(getCommand(name)) {
            "plugin.yml에 '$name' 명령어가 등록되어 있지 않습니다."
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
        var migratedCount = 0
        var skippedCount = 0

        for (playerName in playerConfig.getKeys(false)) {
            val legacyCashPath = "$playerName.$LEGACY_PLAYER_CASH_KEY"
            if (!playerConfig.contains(legacyCashPath)) {
                skippedCount++
                continue
            }

            val legacyCash = playerConfig.getInt(legacyCashPath)
            if (cashRepository.getCash(playerName) == 0L && legacyCash > 0) {
                cashRepository.setCash(playerName, legacyCash.toLong())
                migratedCount++
            } else {
                skippedCount++
            }
        }

        settingConfig.set("database.migrate-player-yml", false)
        ConfigManager.saveConfigs()
        logger.info("기존 캐시 데이터 이전 완료: migrated=$migratedCount, skipped=$skippedCount")
    }
}
