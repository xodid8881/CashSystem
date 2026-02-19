package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.config.MessageManager

class PurchaseGUI(private val player: Player) {
    private val cashSystemConfig = ConfigManager.getConfig("cash-system")
    private val playerConfig = ConfigManager.getConfig("player")
    private val shopName = playerConfig.getString("${player.name}.구매상점") ?: "상점"
    private val inv: Inventory = Bukkit.createInventory(
        null,
        9,
        MessageManager.message("gui.title.purchase_confirm", mapOf("shop" to shopName))
    )

    init {
        fillItems()
    }

    fun open(target: Player) {
        target.openInventory(inv)
    }

    private fun fillItems() {
        val page = playerConfig.getInt("${player.name}.페이지", 0)
        val slot = playerConfig.getInt("${player.name}.구매슬롯", 0)
        val amount = cashSystemConfig.getLong("캐시상점.$shopName.금액.$page.$slot", 0L)
        val product = cashSystemConfig.getItemStack("캐시상점.$shopName.물품.$page.$slot")?.clone()
        product?.editMeta {
            val lore = it.lore?.toMutableList() ?: mutableListOf()
            lore.addAll(MessageManager.messageList("gui.purchase.preview_lore", mapOf("amount" to amount)))
            it.lore = lore
        }
        inv.setItem(4, product)

        inv.setItem(0, GuiDesign.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        inv.setItem(1, GuiDesign.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        inv.setItem(3, GuiDesign.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        inv.setItem(5, GuiDesign.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        inv.setItem(7, GuiDesign.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        inv.setItem(8, GuiDesign.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        inv.setItem(2, GuiDesign.createItem(Material.EMERALD, MessageManager.message("gui.button.buy_name"), MessageManager.messageList("gui.button.buy_lore")))
        inv.setItem(6, GuiDesign.createItem(Material.BARRIER, MessageManager.message("gui.button.cancel_name"), MessageManager.messageList("gui.button.cancel_lore")))
    }
}
