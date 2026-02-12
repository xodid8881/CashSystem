package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.hwabeag.cashsystem.config.ConfigManager

class CashShopItemSettingGUI(private val player: Player) {
    private val cashSystemConfig = ConfigManager.getConfig("cash-system")
    private val playerConfig = ConfigManager.getConfig("player")
    private val shopName = playerConfig.getString("${player.name}.설정상점") ?: "상점"
    private val line = cashSystemConfig.getInt("캐시상점.$shopName.라인", 1).coerceIn(1, 5)
    private val inv: Inventory = Bukkit.createInventory(null, line * 9 + 9, "${shopName}물품설정")

    init {
        fillItems()
        addPageButtons()
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

    private fun addPageButtons() {
        val previous = ItemStack(Material.PAPER)
        previous.editMeta {
            it.setDisplayName(ChatColor.GREEN.toString() + "이전 페이지")
            it.lore = listOf("${ChatColor.GREEN}- ${ChatColor.WHITE}클릭 시 이전 페이지로 이동합니다.")
        }
        inv.setItem(inv.size - 3, previous)

        val next = ItemStack(Material.PAPER)
        next.editMeta {
            it.setDisplayName(ChatColor.GREEN.toString() + "다음 페이지")
            it.lore = listOf("${ChatColor.GREEN}- ${ChatColor.WHITE}클릭 시 다음 페이지로 이동합니다.")
        }
        inv.setItem(inv.size - 1, next)
    }
}

