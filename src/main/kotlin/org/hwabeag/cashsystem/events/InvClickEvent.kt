package org.hwabeag.cashsystem.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.config.MessageManager
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI
import org.hwabeag.cashsystem.inventorys.CashShopGUI
import org.hwabeag.cashsystem.inventorys.CashShopItemSettingGUI
import org.hwabeag.cashsystem.inventorys.CashShopListGUI
import org.hwabeag.cashsystem.inventorys.CashShopPriceEditorGUI
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

        if (title == MessageManager.message("gui.title.shop_list")) {
            event.isCancelled = true
            if (displayName == MessageManager.message("gui.button.prev_name")) {
                val pagePath = "$name.상점목록페이지"
                val page = playerConfig.getInt(pagePath, 0)
                playerConfig.set(pagePath, (page - 1).coerceAtLeast(0))
                ConfigManager.saveConfigs()
                CashShopListGUI(player, playerConfig.getInt(pagePath, 0)).open(player)
                return
            }
            if (displayName == MessageManager.message("gui.button.next_name")) {
                val pagePath = "$name.상점목록페이지"
                val page = playerConfig.getInt(pagePath, 0)
                playerConfig.set(pagePath, page + 1)
                ConfigManager.saveConfigs()
                CashShopListGUI(player, playerConfig.getInt(pagePath, 0)).open(player)
                return
            }

            val shopName = CashShopListGUI.resolveShopName(displayName)
            if (shopName.isBlank()) {
                return
            }
            if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                player.sendMessage("$prefix ${MessageManager.message("shop.not_found")}")
                return
            }

            playerConfig.set("$name.페이지", 0)
            playerConfig.set("$name.구매상점", shopName)
            ConfigManager.saveConfigs()
            CashShopGUI(player).open(player)
            return
        }

        val settingShop = playerConfig.getString("$name.설정상점") ?: "상점"
        if (title == MessageManager.message("gui.title.item_setting", mapOf("shop" to settingShop))) {
            val line = cashSystemConfig.getInt("캐시상점.$settingShop.라인", 1).coerceIn(1, 5)
            if (event.slot >= line * 9) {
                event.isCancelled = true
            }
            if (displayName == MessageManager.message("gui.button.prev_name")) {
                changePage(player, -1)
                CashShopItemSettingGUI(player).open(player)
            } else if (displayName == MessageManager.message("gui.button.next_name")) {
                changePage(player, 1)
                CashShopItemSettingGUI(player).open(player)
            }
            return
        }

        if (title == MessageManager.message("gui.title.amount_setting", mapOf("shop" to settingShop))) {
            event.isCancelled = true
            val line = cashSystemConfig.getInt("캐시상점.$settingShop.라인", 1).coerceIn(1, 5)
            if (displayName == MessageManager.message("gui.button.prev_name")) {
                changePage(player, -1)
                CashShopAmountSettingGUI(player).open(player)
                return
            }
            if (displayName == MessageManager.message("gui.button.next_name")) {
                changePage(player, 1)
                CashShopAmountSettingGUI(player).open(player)
                return
            }
            if (event.slot >= line * 9) {
                return
            }

            val page = playerConfig.getInt("$name.페이지", 0)
            val item = cashSystemConfig.getItemStack("캐시상점.$settingShop.물품.$page.${event.slot}") ?: return
            val currentAmount = cashSystemConfig.getLong("캐시상점.$settingShop.금액.$page.${event.slot}", 0L)
            openPriceEditor(
                player = player,
                shopName = settingShop,
                page = page,
                slot = event.slot,
                amount = currentAmount,
                previewType = item.type
            )
            return
        }

        val editorShop = playerConfig.getString("$name.가격설정상점")
        if (editorShop != null && title == MessageManager.message("gui.title.price_editor", mapOf("shop" to editorShop))) {
            event.isCancelled = true
            val page = playerConfig.getInt("$name.가격설정페이지", 0)
            val slot = playerConfig.getInt("$name.가격설정슬롯", -1)
            val current = playerConfig.getLong("$name.가격설정값", 0L)
            if (slot < 0) {
                return
            }

            when (displayName) {
                MessageManager.message("gui.price_editor.minus_1000_name") -> updatePriceAndRefresh(player, editorShop, page, slot, (current - 1000L).coerceAtLeast(0L))
                MessageManager.message("gui.price_editor.minus_100_name") -> updatePriceAndRefresh(player, editorShop, page, slot, (current - 100L).coerceAtLeast(0L))
                MessageManager.message("gui.price_editor.minus_10_name") -> updatePriceAndRefresh(player, editorShop, page, slot, (current - 10L).coerceAtLeast(0L))
                MessageManager.message("gui.price_editor.plus_10_name") -> updatePriceAndRefresh(player, editorShop, page, slot, current + 10L)
                MessageManager.message("gui.price_editor.plus_100_name") -> updatePriceAndRefresh(player, editorShop, page, slot, current + 100L)
                MessageManager.message("gui.price_editor.plus_1000_name") -> updatePriceAndRefresh(player, editorShop, page, slot, current + 1000L)
                MessageManager.message("gui.price_editor.reset_name") -> updatePriceAndRefresh(player, editorShop, page, slot, 0L)
                MessageManager.message("gui.price_editor.back_name") -> CashShopAmountSettingGUI(player).open(player)
                MessageManager.message("gui.price_editor.save_name") -> {
                    cashSystemConfig.set("캐시상점.$editorShop.금액.$page.$slot", current)
                    ConfigManager.saveConfigs()
                    player.sendMessage("$prefix ${MessageManager.message("amount.updated", mapOf("amount" to current))}")
                    CashShopAmountSettingGUI(player).open(player)
                }
            }
            return
        }

        val buyShop = playerConfig.getString("$name.구매상점") ?: "상점"
        if (title == MessageManager.message("gui.title.shop", mapOf("shop" to buyShop))) {
            event.isCancelled = true
            if (displayName == MessageManager.message("gui.button.prev_name")) {
                changePage(player, -1)
                CashShopGUI(player).open(player)
                return
            }
            if (displayName == MessageManager.message("gui.button.next_name")) {
                changePage(player, 1)
                CashShopGUI(player).open(player)
                return
            }

            playerConfig.set("$name.구매슬롯", event.slot)
            ConfigManager.saveConfigs()
            PurchaseGUI(player).open(player)
            return
        }

        if (title == MessageManager.message("gui.title.purchase_confirm", mapOf("shop" to buyShop))) {
            event.isCancelled = true
            if (displayName == MessageManager.message("gui.button.buy_name")) {
                val page = playerConfig.getInt("$name.페이지", 0)
                val slot = playerConfig.getInt("$name.구매슬롯", 0)
                val amount = cashSystemConfig.getLong("캐시상점.$buyShop.금액.$page.$slot", 0L)
                val item = cashSystemConfig.getItemStack("캐시상점.$buyShop.물품.$page.$slot")

                if (item == null || amount <= 0) {
                    player.closeInventory()
                    player.sendMessage("$prefix ${MessageManager.message("purchase.invalid_product")}")
                    return
                }

                val result = CashSystem.cashRepository.takeCash(name, amount)
                if (result == null) {
                    player.closeInventory()
                    player.sendMessage("$prefix ${MessageManager.message("purchase.not_enough_cash")}")
                    player.sendMessage(
                        "$prefix " + MessageManager.message(
                            "purchase.current_cash",
                            mapOf("amount" to CashSystem.cashRepository.getCash(name))
                        )
                    )
                    return
                }

                player.inventory.addItem(item)
                player.closeInventory()
                player.sendMessage("$prefix ${MessageManager.message("purchase.success")}")
                player.sendMessage(
                    "$prefix " + MessageManager.message(
                        "purchase.current_cash",
                        mapOf("amount" to result)
                    )
                )
                return
            }
            if (displayName == MessageManager.message("gui.button.cancel_name")) {
                player.closeInventory()
                player.sendMessage("$prefix ${MessageManager.message("purchase.cancelled")}")
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

    private fun openPriceEditor(player: Player, shopName: String, page: Int, slot: Int, amount: Long, previewType: org.bukkit.Material) {
        val playerConfig = ConfigManager.getConfig("player")
        val name = player.name
        playerConfig.set("$name.설정상점", shopName)
        playerConfig.set("$name.페이지", page)
        playerConfig.set("$name.설정슬롯", slot)
        playerConfig.set("$name.금액설정", true)
        playerConfig.set("$name.가격설정상점", shopName)
        playerConfig.set("$name.가격설정페이지", page)
        playerConfig.set("$name.가격설정슬롯", slot)
        playerConfig.set("$name.가격설정값", amount)
        ConfigManager.saveConfigs()
        CashShopPriceEditorGUI(player, shopName, page, slot, amount, previewType).open(player)
    }

    private fun updatePriceAndRefresh(player: Player, shopName: String, page: Int, slot: Int, amount: Long) {
        val playerConfig = ConfigManager.getConfig("player")
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val name = player.name
        playerConfig.set("$name.가격설정값", amount)
        ConfigManager.saveConfigs()
        val previewType = cashSystemConfig.getItemStack("캐시상점.$shopName.물품.$page.$slot")?.type ?: org.bukkit.Material.PAPER
        CashShopPriceEditorGUI(player, shopName, page, slot, amount, previewType).open(player)
    }
}
