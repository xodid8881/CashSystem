package org.hwabeag.cashsystem.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.config.MessageManager
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI
import org.hwabeag.cashsystem.inventorys.CashShopGUI
import org.hwabeag.cashsystem.inventorys.CashShopItemSettingGUI
import org.hwabeag.cashsystem.inventorys.CashShopListGUI

class ShopCommand(private val plugin: CashSystem) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        return when (args.size) {
            1 -> mutableListOf("열기", "목록", "생성", "삭제", "물품설정", "금액설정")
            2 -> cashSystemConfig.getConfigurationSection("캐시상점")?.getKeys(false)?.toMutableList() ?: mutableListOf()
            else -> mutableListOf()
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val prefix = plugin.prefix()
        if (sender !is Player) {
            sender.sendMessage("$prefix ${MessageManager.message("common.player_only")}")
            return true
        }

        if (args.isEmpty()) {
            openShopList(sender)
            return true
        }

        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val playerConfig = ConfigManager.getConfig("player")
        val action = args[0]
        val shopName = args.getOrNull(1)

        when (action) {
            "목록" -> {
                openShopList(sender)
            }

            "열기" -> {
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.enter_shop_name")}")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.not_found")}")
                    return true
                }
                playerConfig.set("${sender.name}.페이지", 0)
                playerConfig.set("${sender.name}.구매상점", shopName)
                ConfigManager.saveConfigs()
                CashShopGUI(sender).open(sender)
            }

            "생성" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix ${MessageManager.message("common.no_permission")}")
                    return true
                }
                val line = args.getOrNull(2)?.toIntOrNull()
                if (shopName.isNullOrBlank() || line == null) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.usage_create")}")
                    return true
                }
                if (line !in 1..5) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.invalid_line")}")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") != null) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.already_exists")}")
                    return true
                }
                cashSystemConfig.set("캐시상점.$shopName.라인", line)
                ConfigManager.saveConfigs()
                sender.sendMessage(
                    "$prefix " + MessageManager.message(
                        "shop.created",
                        mapOf("shop" to shopName)
                    )
                )
            }

            "삭제" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix ${MessageManager.message("common.no_permission")}")
                    return true
                }
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.usage_delete")}")
                    return true
                }
                cashSystemConfig.set("캐시상점.$shopName", null)
                ConfigManager.saveConfigs()
                sender.sendMessage(
                    "$prefix " + MessageManager.message(
                        "shop.deleted",
                        mapOf("shop" to shopName)
                    )
                )
            }

            "물품설정" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix ${MessageManager.message("common.no_permission")}")
                    return true
                }
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.usage_item_setting")}")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.not_found")}")
                    return true
                }
                playerConfig.set("${sender.name}.페이지", 0)
                playerConfig.set("${sender.name}.설정상점", shopName)
                ConfigManager.saveConfigs()
                CashShopItemSettingGUI(sender).open(sender)
            }

            "금액설정" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix ${MessageManager.message("common.no_permission")}")
                    return true
                }
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.usage_amount_setting")}")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                    sender.sendMessage("$prefix ${MessageManager.message("shop.not_found")}")
                    return true
                }
                playerConfig.set("${sender.name}.페이지", 0)
                playerConfig.set("${sender.name}.설정상점", shopName)
                ConfigManager.saveConfigs()
                CashShopAmountSettingGUI(sender).open(sender)
            }

            else -> sendHelp(sender, prefix)
        }
        return true
    }

    private fun openShopList(sender: Player) {
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val shops = cashSystemConfig.getConfigurationSection("캐시상점")?.getKeys(false) ?: emptySet()
        if (shops.isEmpty()) {
            sender.sendMessage("${plugin.prefix()} ${MessageManager.message("shop.list_empty")}")
            return
        }

        val playerConfig = ConfigManager.getConfig("player")
        val page = playerConfig.getInt("${sender.name}.상점목록페이지", 0).coerceAtLeast(0)
        CashShopListGUI(sender, page).open(sender)
    }

    private fun sendHelp(sender: Player, prefix: String) {
        sender.sendMessage("$prefix ${MessageManager.message("shop.help.list")}")
        sender.sendMessage("$prefix ${MessageManager.message("shop.help.open")}")
        if (sender.isOp) {
            sender.sendMessage("$prefix ${MessageManager.message("shop.help.create")}")
            sender.sendMessage("$prefix ${MessageManager.message("shop.help.delete")}")
            sender.sendMessage("$prefix ${MessageManager.message("shop.help.item_setting")}")
            sender.sendMessage("$prefix ${MessageManager.message("shop.help.amount_setting")}")
        }
    }
}
