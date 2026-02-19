package org.hwabeag.cashsystem.expansions

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.hwabeag.cashsystem.CashSystem

class CashSystemExpansion(private val plugin: CashSystem) : PlaceholderExpansion() {
    override fun persist(): Boolean = true

    override fun canRegister(): Boolean = true

    override fun getAuthor(): String = plugin.description.authors.joinToString(", ")

    override fun getIdentifier(): String = "cash"

    override fun getVersion(): String = plugin.description.version

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (!params.equals("get", ignoreCase = true) || player?.name == null) {
            return null
        }
        return CashSystem.cashRepository.getCash(player.name!!).toString()
    }
}

