package org.hwabeag.cashsystem.inventorys

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.hwabeag.cashsystem.config.MessageManager

object GuiDesign {
    fun applyBottomBar(inventory: Inventory, contentSlots: Set<Int>) {
        val start = inventory.size - 9
        val end = inventory.size - 1
        for (slot in start..end) {
            if (slot in contentSlots) {
                continue
            }
            inventory.setItem(slot, createItem(Material.BLACK_STAINED_GLASS_PANE, " ", emptyList()))
        }
    }

    fun createNavigationButton(isNext: Boolean): ItemStack {
        val keyPrefix = if (isNext) "gui.button.next" else "gui.button.prev"
        return createItem(
            Material.ARROW,
            MessageManager.message("${keyPrefix}_name"),
            MessageManager.messageList("${keyPrefix}_lore")
        )
    }

    fun createStatusItem(nameKey: String, loreKey: String, placeholders: Map<String, Any>): ItemStack {
        return createItem(
            Material.KNOWLEDGE_BOOK,
            MessageManager.message(nameKey, placeholders),
            MessageManager.messageList(loreKey, placeholders)
        )
    }

    fun createItem(material: Material, name: String, lore: List<String>): ItemStack {
        val item = ItemStack(material)
        item.editMeta {
            it.setDisplayName(name)
            it.lore = lore
        }
        return item
    }
}
