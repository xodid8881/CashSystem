package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.hwabeag.cashsystem.config.MessageManager

class CashShopPriceEditorGUI(
    private val player: Player,
    private val shopName: String,
    private val page: Int,
    private val slot: Int,
    private val amount: Long,
    private val previewMaterial: Material
) {
    private val inv: Inventory = Bukkit.createInventory(
        null,
        27,
        MessageManager.message("gui.title.price_editor", mapOf("shop" to shopName))
    )

    init {
        render()
    }

    fun open(target: Player) {
        target.openInventory(inv)
    }

    private fun render() {
        inv.setItem(10, GuiDesign.createItem(Material.RED_STAINED_GLASS, MessageManager.message("gui.price_editor.minus_1000_name"), emptyList()))
        inv.setItem(11, GuiDesign.createItem(Material.RED_STAINED_GLASS, MessageManager.message("gui.price_editor.minus_100_name"), emptyList()))
        inv.setItem(12, GuiDesign.createItem(Material.RED_STAINED_GLASS, MessageManager.message("gui.price_editor.minus_10_name"), emptyList()))
        inv.setItem(14, GuiDesign.createItem(Material.LIME_STAINED_GLASS, MessageManager.message("gui.price_editor.plus_10_name"), emptyList()))
        inv.setItem(15, GuiDesign.createItem(Material.LIME_STAINED_GLASS, MessageManager.message("gui.price_editor.plus_100_name"), emptyList()))
        inv.setItem(16, GuiDesign.createItem(Material.LIME_STAINED_GLASS, MessageManager.message("gui.price_editor.plus_1000_name"), emptyList()))
        inv.setItem(18, GuiDesign.createItem(Material.BARRIER, MessageManager.message("gui.price_editor.reset_name"), MessageManager.messageList("gui.price_editor.reset_lore")))
        inv.setItem(22, GuiDesign.createItem(Material.ARROW, MessageManager.message("gui.price_editor.back_name"), MessageManager.messageList("gui.price_editor.back_lore")))
        inv.setItem(26, GuiDesign.createItem(Material.EMERALD_BLOCK, MessageManager.message("gui.price_editor.save_name"), MessageManager.messageList("gui.price_editor.save_lore")))

        inv.setItem(
            4,
            GuiDesign.createStatusItem(
                "gui.price_editor.status_name",
                "gui.price_editor.status_lore",
                mapOf("shop" to shopName, "page" to (page + 1), "slot" to slot, "amount" to amount)
            )
        )

        val preview = GuiDesign.createItem(
            previewMaterial,
            MessageManager.message("gui.price_editor.preview_name"),
            MessageManager.messageList("gui.price_editor.preview_lore", mapOf("amount" to amount))
        )
        inv.setItem(13, preview)

        GuiDesign.applyBottomBar(inv, setOf(4, 10, 11, 12, 13, 14, 15, 16, 18, 22, 26))
    }
}
