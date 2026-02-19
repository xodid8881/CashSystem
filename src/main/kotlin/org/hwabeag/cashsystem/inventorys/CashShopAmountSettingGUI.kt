package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.config.MessageManager

class CashShopAmountSettingGUI(private val player: Player) {
    private val cashSystemConfig = ConfigManager.getConfig("cash-system")
    private val playerConfig = ConfigManager.getConfig("player")
    private val shopName = playerConfig.getString("${player.name}.설정상점") ?: "상점"
    private val line = cashSystemConfig.getInt("캐시상점.$shopName.라인", 1).coerceIn(1, 5)
    private val inv: Inventory = Bukkit.createInventory(
        null,
        line * 9 + 9,
        MessageManager.message("gui.title.amount_setting", mapOf("shop" to shopName))
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
            val source = cashSystemConfig.getItemStack("캐시상점.$shopName.물품.$page.$slot")?.clone() ?: continue
            val meta = source.itemMeta ?: continue
            val lore = mutableListOf<String>()
            meta.lore?.forEach { lore.add(ChatColor.translateAlternateColorCodes('&', it)) }
            val amount = cashSystemConfig.getLong("캐시상점.$shopName.금액.$page.$slot", 0L)
            lore.addAll(MessageManager.messageList("gui.shop_item_lore", mapOf("amount" to amount)))
            meta.lore = lore
            source.itemMeta = meta
            inv.setItem(slot, source)
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
                nameKey = "gui.footer.amount_setting_status_name",
                loreKey = "gui.footer.amount_setting_status_lore",
                placeholders = mapOf("page" to page)
            )
        )
    }
}
