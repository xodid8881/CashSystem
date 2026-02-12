package org.hwabeag.cashsystem.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.ConfigManager

class JoinEvent : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val name = player.name
        val playerConfig = ConfigManager.getConfig("player")

        if (playerConfig.getConfigurationSection(name) == null) {
            playerConfig.createSection(name)
        }
        if (!playerConfig.contains("$name.페이지")) playerConfig.set("$name.페이지", 0)
        if (!playerConfig.contains("$name.구매상점")) playerConfig.set("$name.구매상점", "없음")
        if (!playerConfig.contains("$name.구매슬롯")) playerConfig.set("$name.구매슬롯", 0)
        if (!playerConfig.contains("$name.설정상점")) playerConfig.set("$name.설정상점", "없음")
        if (!playerConfig.contains("$name.금액설정")) playerConfig.set("$name.금액설정", false)
        if (!playerConfig.contains("$name.설정슬롯")) playerConfig.set("$name.설정슬롯", 0)
        ConfigManager.saveConfigs()

        CashSystem.cashRepository.initializePlayer(name)
    }
}

