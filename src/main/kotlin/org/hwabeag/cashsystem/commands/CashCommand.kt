package org.hwabeag.cashsystem.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.hwabeag.cashsystem.CashSystem

class CashCommand(private val plugin: CashSystem) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return when (args.size) {
            1 -> mutableListOf("지급", "회수", "설정", "확인")
            2 -> Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
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
        if (args.isEmpty()) {
            sendHelp(sender, prefix)
            return true
        }

        val action = args[0].lowercase()
        if (action == "확인") {
            val targetName = if (args.size >= 2) args[1] else (sender as? Player)?.name
            if (targetName == null) {
                sender.sendMessage("$prefix 콘솔에서는 /캐시 확인 <닉네임> 형식으로 사용하세요.")
                return true
            }
            val cash = CashSystem.cashRepository.getCash(targetName)
            sender.sendMessage("$prefix $targetName 님의 보유 캐시: ${cash}원")
            return true
        }

        if (!sender.isOp) {
            sender.sendMessage("$prefix 권한이 없습니다.")
            return true
        }

        if (args.size < 3) {
            sendHelp(sender, prefix)
            return true
        }

        val targetName = args[1]
        val amount = args[2].toLongOrNull()
        if (amount == null || amount < 0L) {
            sender.sendMessage("$prefix 금액은 0 이상의 숫자여야 합니다.")
            return true
        }

        when (action) {
            "지급" -> {
                val result = CashSystem.cashRepository.addCash(targetName, amount)
                sender.sendMessage("$prefix $targetName 님에게 ${amount}원을 지급했습니다. (현재: ${result}원)")
                Bukkit.getPlayerExact(targetName)?.sendMessage("$prefix ${amount}원이 지급되었습니다. (현재: ${result}원)")
            }
            "회수" -> {
                val result = CashSystem.cashRepository.takeCash(targetName, amount)
                if (result == null) {
                    sender.sendMessage("$prefix $targetName 님의 캐시가 부족하여 회수할 수 없습니다.")
                    return true
                }
                sender.sendMessage("$prefix $targetName 님에게서 ${amount}원을 회수했습니다. (현재: ${result}원)")
                Bukkit.getPlayerExact(targetName)?.sendMessage("$prefix ${amount}원이 회수되었습니다. (현재: ${result}원)")
            }
            "설정" -> {
                CashSystem.cashRepository.setCash(targetName, amount)
                sender.sendMessage("$prefix $targetName 님의 캐시를 ${amount}원으로 설정했습니다.")
                Bukkit.getPlayerExact(targetName)?.sendMessage("$prefix 캐시가 ${amount}원으로 설정되었습니다.")
            }
            else -> sendHelp(sender, prefix)
        }
        return true
    }

    private fun sendHelp(sender: CommandSender, prefix: String) {
        if (sender.isOp) {
            sender.sendMessage("$prefix /캐시 지급 <닉네임> <금액>")
            sender.sendMessage("$prefix /캐시 회수 <닉네임> <금액>")
            sender.sendMessage("$prefix /캐시 설정 <닉네임> <금액>")
        }
        sender.sendMessage("$prefix /캐시 확인 [닉네임]")
    }
}

