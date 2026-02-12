package org.hwabeag.cashsystem.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.hwabeag.cashsystem.config.ConfigManager

class InvCloseEvent : Listener {
    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val title = event.view.title
        if (!title.endsWith("물품설정")) {
            return
        }

        val player = event.player
        val name = player.name
        val playerConfig = ConfigManager.getConfig("player")
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val shopName = playerConfig.getString("$name.설정상점") ?: return
        val line = cashSystemConfig.getInt("캐시상점.$shopName.라인", 1).coerceIn(1, 5)
        val page = playerConfig.getInt("$name.페이지", 0)
        val maxSlot = line * 9 - 1

        for (slot in 0..maxSlot) {
            val item = event.inventory.getItem(slot)
            val itemPath = "캐시상점.$shopName.물품.$page.$slot"
            val amountPath = "캐시상점.$shopName.금액.$page.$slot"

            if (item != null) {
                cashSystemConfig.set(itemPath, item)
                if (!cashSystemConfig.contains(amountPath)) {
                    cashSystemConfig.set(amountPath, 0)
                }
            } else if (cashSystemConfig.contains(itemPath)) {
                cashSystemConfig.set(itemPath, null)
                cashSystemConfig.set(amountPath, null)
            }
        }

        playerConfig.set("$name.설정상점", "없음")
        ConfigManager.saveConfigs()
    }
}

