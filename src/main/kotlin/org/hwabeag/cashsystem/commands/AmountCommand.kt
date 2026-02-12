package org.hwabeag.cashsystem.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.ConfigManager
import org.hwabeag.cashsystem.inventorys.CashShopAmountSettingGUI

class AmountCommand(private val plugin: CashSystem) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> = mutableListOf()

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
        if (!sender.isOp) {
            sender.sendMessage("$prefix 권한이 없습니다.")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("$prefix /금액 <금액>")
            return true
        }

        val amount = args[0].toLongOrNull()
        if (amount == null || amount < 0L) {
            sender.sendMessage("$prefix 금액은 0 이상의 숫자여야 합니다.")
            return true
        }

        val playerConfig = ConfigManager.getConfig("player")
        val cashSystemConfig = ConfigManager.getConfig("cash-system")
        val name = sender.name

        if (!playerConfig.getBoolean("$name.금액설정", false)) {
            sender.sendMessage("$prefix 금액설정 GUI에서 아이템을 먼저 선택하세요.")
            return true
        }

        val page = playerConfig.getInt("$name.페이지", 0)
        val shopName = playerConfig.getString("$name.설정상점")
        val slot = playerConfig.getInt("$name.설정슬롯", -1)
        if (shopName.isNullOrBlank() || slot < 0) {
            sender.sendMessage("$prefix 설정 상태를 찾을 수 없습니다.")
            return true
        }

        cashSystemConfig.set("캐시상점.$shopName.금액.$page.$slot", amount)
        playerConfig.set("$name.금액설정", false)
        playerConfig.set("$name.설정슬롯", 0)
        ConfigManager.saveConfigs()

        sender.sendMessage("$prefix 구매 금액을 ${amount}원으로 설정했습니다.")
        CashShopAmountSettingGUI(sender).open(sender)
        return true
    }
}

