package org.hwabeag.cashsystem.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI
import org.hwabeag.cashsystem.inventorys.CashShopGUI
import org.hwabeag.cashsystem.inventorys.CashShopItemSettingGUI

class ShopCommand(private val plugin: CashSystem) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        return when (args.size) {
            1 -> mutableListOf("열기", "생성", "삭제", "물품설정", "금액설정")
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
            sender.sendMessage("$prefix 플레이어만 사용할 수 있습니다.")
            return true
        }

        if (args.isEmpty()) {
            sendHelp(sender, prefix)
            return true
        }

        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val playerConfig = ConfigManager.getConfig("player")
        val action = args[0]
        val shopName = args.getOrNull(1)

        when (action) {
            "열기" -> {
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix 상점명을 입력하세요.")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                    sender.sendMessage("$prefix 해당 상점이 존재하지 않습니다.")
                    return true
                }
                playerConfig.set("${sender.name}.페이지", 0)
                playerConfig.set("${sender.name}.구매상점", shopName)
                ConfigManager.saveConfigs()
                CashShopGUI(sender).open(sender)
            }
            "생성" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 권한이 없습니다.")
                    return true
                }
                val line = args.getOrNull(2)?.toIntOrNull()
                if (shopName.isNullOrBlank() || line == null) {
                    sender.sendMessage("$prefix /캐시상점 생성 <상점명> <라인(1~5)>")
                    return true
                }
                if (line !in 1..5) {
                    sender.sendMessage("$prefix 라인은 1~5 범위만 가능합니다.")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") != null) {
                    sender.sendMessage("$prefix 이미 존재하는 상점명입니다.")
                    return true
                }
                cashSystemConfig.set("캐시상점.$shopName.라인", line)
                ConfigManager.saveConfigs()
                sender.sendMessage("$prefix $shopName 상점을 생성했습니다.")
            }
            "삭제" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 권한이 없습니다.")
                    return true
                }
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix /캐시상점 삭제 <상점명>")
                    return true
                }
                cashSystemConfig.set("캐시상점.$shopName", null)
                ConfigManager.saveConfigs()
                sender.sendMessage("$prefix $shopName 상점을 삭제했습니다.")
            }
            "물품설정" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 권한이 없습니다.")
                    return true
                }
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix /캐시상점 물품설정 <상점명>")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                    sender.sendMessage("$prefix 해당 상점이 존재하지 않습니다.")
                    return true
                }
                playerConfig.set("${sender.name}.페이지", 0)
                playerConfig.set("${sender.name}.설정상점", shopName)
                ConfigManager.saveConfigs()
                CashShopItemSettingGUI(sender).open(sender)
            }
            "금액설정" -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 권한이 없습니다.")
                    return true
                }
                if (shopName.isNullOrBlank()) {
                    sender.sendMessage("$prefix /캐시상점 금액설정 <상점명>")
                    return true
                }
                if (cashSystemConfig.getConfigurationSection("캐시상점.$shopName") == null) {
                    sender.sendMessage("$prefix 해당 상점이 존재하지 않습니다.")
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

    private fun sendHelp(sender: Player, prefix: String) {
        sender.sendMessage("$prefix /캐시상점 열기 <상점명>")
        if (sender.isOp) {
            sender.sendMessage("$prefix /캐시상점 생성 <상점명> <라인>")
            sender.sendMessage("$prefix /캐시상점 삭제 <상점명>")
            sender.sendMessage("$prefix /캐시상점 물품설정 <상점명>")
            sender.sendMessage("$prefix /캐시상점 금액설정 <상점명>")
        }
    }
}

