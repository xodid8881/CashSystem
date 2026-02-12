package org.hwabeag.cashsystem.events

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI
import org.hwabeag.cashsystem.inventorys.CashShopGUI
import org.hwabeag.cashsystem.inventorys.CashShopItemSettingGUI
import org.hwabeag.cashsystem.inventorys.PurchaseGUI

class InvClickEvent(private val plugin: CashSystem) : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val clicked = event.currentItem ?: return
        val player = event.whoClicked as? Player ?: return
        val name = player.name
        val title = event.view.title
        val displayName = clicked.itemMeta?.displayName ?: return

        val playerConfig = ConfigManager.getConfig("player")
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val prefix = plugin.prefix()

        val settingShop = playerConfig.getString("$name.설정상점") ?: "상점"
        if (title == "${settingShop}물품설정") {
            if (displayName == "${ChatColor.GREEN}이전 페이지") {
                changePage(player, -1)
                CashShopItemSettingGUI(player).open(player)
            } else if (displayName == "${ChatColor.GREEN}다음 페이지") {
                changePage(player, 1)
                CashShopItemSettingGUI(player).open(player)
            }
            return
        }

        if (title == "${settingShop}금액설정") {
            event.isCancelled = true
            if (displayName == "${ChatColor.GREEN}이전 페이지") {
                changePage(player, -1)
                CashShopAmountSettingGUI(player).open(player)
                return
            }
            if (displayName == "${ChatColor.GREEN}다음 페이지") {
                changePage(player, 1)
                CashShopAmountSettingGUI(player).open(player)
                return
            }

            playerConfig.set("$name.금액설정", true)
            playerConfig.set("$name.설정슬롯", event.slot)
            ConfigManager.saveConfigs()
            player.closeInventory()
            player.sendMessage("$prefix 금액은 /금액 명령어로 설정해주세요.")
            return
        }

        val buyShop = playerConfig.getString("$name.구매상점") ?: "상점"
        if (title == "${buyShop}캐시상점") {
            event.isCancelled = true
            if (displayName == "${ChatColor.GREEN}이전 페이지") {
                changePage(player, -1)
                CashShopGUI(player).open(player)
                return
            }
            if (displayName == "${ChatColor.GREEN}다음 페이지") {
                changePage(player, 1)
                CashShopGUI(player).open(player)
                return
            }

            playerConfig.set("$name.구매슬롯", event.slot)
            ConfigManager.saveConfigs()
            PurchaseGUI(player).open(player)
            return
        }

        if (title == "${buyShop}구매확인") {
            event.isCancelled = true
            if (displayName == "${ChatColor.GREEN}구매하기") {
                val page = playerConfig.getInt("$name.페이지", 0)
                val slot = playerConfig.getInt("$name.구매슬롯", 0)
                val amount = cashSystemConfig.getLong("캐시상점.$buyShop.금액.$page.$slot", 0L)
                val item = cashSystemConfig.getItemStack("캐시상점.$buyShop.물품.$page.$slot")

                if (item == null || amount <= 0) {
                    player.closeInventory()
                    player.sendMessage("$prefix 해당 물품은 구매할 수 없습니다.")
                    return
                }

                val result = CashSystem.cashRepository.takeCash(name, amount)
                if (result == null) {
                    player.closeInventory()
                    player.sendMessage("$prefix 캐시가 부족합니다.")
                    player.sendMessage("$prefix 보유 캐시: ${CashSystem.cashRepository.getCash(name)}원")
                    return
                }

                player.inventory.addItem(item)
                player.closeInventory()
                player.sendMessage("$prefix 구매가 완료되었습니다.")
                player.sendMessage("$prefix 보유 캐시: ${result}원")
                return
            }
            if (displayName == "${ChatColor.RED}취소하기") {
                player.closeInventory()
                player.sendMessage("$prefix 구매를 취소했습니다.")
            }
        }
    }

    private fun changePage(player: Player, delta: Int) {
        val playerConfig = ConfigManager.getConfig("player")
        val pagePath = "${player.name}.페이지"
        val page = playerConfig.getInt(pagePath, 0)
        playerConfig.set(pagePath, (page + delta).coerceAtLeast(0))
        ConfigManager.saveConfigs()
    }
}

