package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.config.MessageManager

class CashShopItemSettingGUI(private val player: Player) {
    private val cashSystemConfig = ConfigManager.getConfig("cash-system")
    private val playerConfig = ConfigManager.getConfig("player")
    private val shopName = playerConfig.getString("${player.name}.설정상점") ?: "상점"
    private val line = cashSystemConfig.getInt("캐시상점.$shopName.라인", 1).coerceIn(1, 5)
    private val inv: Inventory = Bukkit.createInventory(
        null,
        line * 9 + 9,
        MessageManager.message("gui.title.item_setting", mapOf("shop" to shopName))
    )

    init {
        fillItems()
        addDesign()
    }

    fun open(target: Player) {
        target.openInventory(inv)
    }

    private fun fillItems() {
        val page = playerConfig.getInt("${player.name}.페이지", 0)
        val section = cashSystemConfig.getConfigurationSection("캐시상점.$shopName.물품.$page") ?: return
        for (key in section.getKeys(false)) {
            val slot = key.toIntOrNull() ?: continue
            val item = cashSystemConfig.getItemStack("캐시상점.$shopName.물품.$page.$slot")?.clone() ?: continue
            inv.setItem(slot, item)
        }
    }

    private fun addDesign() {
        val page = playerConfig.getInt("${player.name}.페이지", 0) + 1
        val statusSlot = inv.size - 5
        val prevSlot = inv.size - 3
        val nextSlot = inv.size - 1

        GuiDesign.applyBottomBar(inv, setOf(statusSlot, prevSlot, nextSlot))
        inv.setItem(prevSlot, GuiDesign.createNavigationButton(isNext = false))
        inv.setItem(nextSlot, GuiDesign.createNavigationButton(isNext = true))
        inv.setItem(
            statusSlot,
            GuiDesign.createStatusItem(
                nameKey = "gui.footer.item_setting_status_name",
                loreKey = "gui.footer.item_setting_status_lore",
                placeholders = mapOf("page" to page)
            )
        )
    }
}
