package org.hwabeag.cashsystem.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.hwabeag.cashsystem.CashSystem
import org.hwabeag.cashsystem.config.MessageManager

class CashCommand(private val plugin: CashSystem) : TabExecutor {
    private val actions = listOf("지급", "회수", "설정", "확인")

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return when (args.size) {
            1 -> actions.toMutableList()
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
                sender.sendMessage("$prefix ${MessageManager.message("cash.console_usage_check")}")
                return true
            }
            val cash = CashSystem.cashRepository.getCash(targetName)
            sender.sendMessage(
                "$prefix " + MessageManager.message(
                    "cash.balance_of",
                    mapOf("player" to targetName, "amount" to cash)
                )
            )
            return true
        }

        if (!sender.isOp) {
            sender.sendMessage("$prefix ${MessageManager.message("common.no_permission")}")
            return true
        }

        if (args.size < 3) {
            sendHelp(sender, prefix)
            return true
        }

        val targetName = args[1]
        val amount = args[2].toLongOrNull()
        if (amount == null || amount < 0L) {
            sender.sendMessage("$prefix ${MessageManager.message("common.invalid_amount")}")
            return true
        }

        when (action) {
            "지급" -> {
                val result = CashSystem.cashRepository.addCash(targetName, amount)
                sender.sendMessage(
                    "$prefix " + MessageManager.message(
                        "cash.admin_give_success",
                        mapOf("player" to targetName, "amount" to amount, "balance" to result)
                    )
                )
                Bukkit.getPlayerExact(targetName)?.sendMessage(
                    "$prefix " + MessageManager.message(
                        "cash.user_give_notice",
                        mapOf("amount" to amount, "balance" to result)
                    )
                )
            }

            "회수" -> {
                val result = CashSystem.cashRepository.takeCash(targetName, amount)
                if (result == null) {
                    sender.sendMessage(
                        "$prefix " + MessageManager.message(
                            "cash.admin_take_failed",
                            mapOf("player" to targetName)
                        )
                    )
                    return true
                }
                sender.sendMessage(
                    "$prefix " + MessageManager.message(
                        "cash.admin_take_success",
                        mapOf("player" to targetName, "amount" to amount, "balance" to result)
                    )
                )
                Bukkit.getPlayerExact(targetName)?.sendMessage(
                    "$prefix " + MessageManager.message(
                        "cash.user_take_notice",
                        mapOf("amount" to amount, "balance" to result)
                    )
                )
            }

            "설정" -> {
                CashSystem.cashRepository.setCash(targetName, amount)
                sender.sendMessage(
                    "$prefix " + MessageManager.message(
                        "cash.admin_set_success",
                        mapOf("player" to targetName, "amount" to amount)
                    )
                )
                Bukkit.getPlayerExact(targetName)?.sendMessage(
                    "$prefix " + MessageManager.message(
                        "cash.user_set_notice",
                        mapOf("amount" to amount)
                    )
                )
            }

            else -> sendHelp(sender, prefix)
        }
        return true
    }

    private fun sendHelp(sender: CommandSender, prefix: String) {
        if (sender.isOp) {
            sender.sendMessage("$prefix ${MessageManager.message("cash.help.give")}")
            sender.sendMessage("$prefix ${MessageManager.message("cash.help.take")}")
            sender.sendMessage("$prefix ${MessageManager.message("cash.help.set")}")
        }
        sender.sendMessage("$prefix ${MessageManager.message("cash.help.check")}")
    }
}
