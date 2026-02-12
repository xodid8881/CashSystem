package org.hwabeag.cashsystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.hwabeag.cashsystem.config.ConfigManager

class PurchaseGUI(private val player: Player) {
    private val cashSystemConfig = ConfigManager.getConfig("cash-system")
    private val playerConfig = ConfigManager.getConfig("player")
    private val shopName = playerConfig.getString("${player.name}.구매상점") ?: "상점"
    private val inv: Inventory = Bukkit.createInventory(null, 9, "${shopName}구매확인")

    init {
        fillItems()
    }

    fun open(target: Player) {
        target.openInventory(inv)
    }

    private fun fillItems() {
        val page = playerConfig.getInt("${player.name}.페이지", 0)
        val slot = playerConfig.getInt("${player.name}.구매슬롯", 0)
        val product = cashSystemConfig.getItemStack("캐시상점.$shopName.물품.$page.$slot")
        inv.setItem(4, product)

        val buy = ItemStack(Material.PAPER)
        buy.editMeta {
            it.setDisplayName(ChatColor.GREEN.toString() + "구매하기")
            it.lore = listOf("${ChatColor.GREEN}- ${ChatColor.WHITE}클릭 시 물품을 구매합니다.")
        }
        inv.setItem(2, buy)

        val cancel = ItemStack(Material.PAPER)
        cancel.editMeta {
            it.setDisplayName(ChatColor.RED.toString() + "취소하기")
            it.lore = listOf("${ChatColor.GREEN}- ${ChatColor.WHITE}클릭 시 구매를 취소합니다.")
        }
        inv.setItem(6, cancel)
    }
}

