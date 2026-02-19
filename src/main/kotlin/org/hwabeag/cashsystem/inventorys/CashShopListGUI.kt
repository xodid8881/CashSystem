package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.config.MessageManager
import kotlin.math.max

class CashShopListGUI(private val player: Player, private val page: Int) {
    private val cashSystemConfig = ConfigManager.getConfig("cash-system")
    private val shopNames = cashSystemConfig.getConfigurationSection("캐시상점")?.getKeys(false)?.sorted() ?: emptyList()
    private val pageSize = 45
    private val totalPages = max(1, (shopNames.size + pageSize - 1) / pageSize)
    private val safePage = page.coerceIn(0, totalPages - 1)
    private val inv: Inventory = Bukkit.createInventory(
        null,
        54,
        MessageManager.message("gui.title.shop_list")
    )

    init {
        fillShops()
        addFooter()
    }

    fun open(target: Player) {
        target.openInventory(inv)
    }

    private fun fillShops() {
        val from = safePage * pageSize
        val to = minOf(shopNames.size, from + pageSize)
        for ((slot, index) in (from until to).withIndex()) {
            val shopName = shopNames[index]
            val line = cashSystemConfig.getInt("캐시상점.$shopName.라인", 1).coerceIn(1, 5)
            val itemCount = countItems(shopName)
            inv.setItem(
                slot,
                GuiDesign.createItem(
                    Material.CHEST,
                    MessageManager.message("gui.shop_list.shop_name", mapOf("shop" to shopName)),
                    MessageManager.messageList(
                        "gui.shop_list.shop_lore",
                        mapOf("line" to line, "items" to itemCount)
                    )
                )
            )
        }
    }

    private fun addFooter() {
        val statusSlot = 49
        val prevSlot = 51
        val nextSlot = 53
        GuiDesign.applyBottomBar(inv, setOf(statusSlot, prevSlot, nextSlot))
        inv.setItem(statusSlot, GuiDesign.createStatusItem(
            nameKey = "gui.footer.shop_list_status_name",
            loreKey = "gui.footer.shop_list_status_lore",
            placeholders = mapOf("page" to (safePage + 1), "total" to totalPages, "shops" to shopNames.size)
        ))
        inv.setItem(prevSlot, GuiDesign.createNavigationButton(isNext = false))
        inv.setItem(nextSlot, GuiDesign.createNavigationButton(isNext = true))
    }

    private fun countItems(shopName: String): Int {
        val itemSection = cashSystemConfig.getConfigurationSection("캐시상점.$shopName.물품") ?: return 0
        var count = 0
        for (pageKey in itemSection.getKeys(false)) {
            val pageSection = itemSection.getConfigurationSection(pageKey) ?: continue
            count += pageSection.getKeys(false).size
        }
        return count
    }

    companion object {
        fun resolveShopName(displayName: String): String {
            return ChatColor.stripColor(displayName)?.trim().orEmpty()
        }
    }
}
