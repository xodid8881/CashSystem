package org.hwabeag.cashsystem.config

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration

object MessageManager {
    fun message(key: String, placeholders: Map<String, Any> = emptyMap()): String {
        var raw = getActiveMessageConfig().getString(key)
            ?: ConfigManager.getConfig("message-kor").getString(key)
            ?: key

        for ((name, value) in placeholders) {
            raw = raw.replace("%$name%", value.toString())
        }

        return ChatColor.translateAlternateColorCodes('&', raw)
    }

    fun messageList(key: String, placeholders: Map<String, Any> = emptyMap()): List<String> {
        var lines = getActiveMessageConfig().getStringList(key)
        if (lines.isEmpty()) {
            lines = ConfigManager.getConfig("message-kor").getStringList(key)
        }

        return lines.map { line ->
            var parsed = line
            for ((name, value) in placeholders) {
                parsed = parsed.replace("%$name%", value.toString())
            }
            ChatColor.translateAlternateColorCodes('&', parsed)
        }
    }

    private fun getActiveMessageConfig(): FileConfiguration {
        val language = ConfigManager.getConfig("setting").getString("language", "kor")?.lowercase() ?: "kor"
        return if (language == "eng") ConfigManager.getConfig("message-eng") else ConfigManager.getConfig("message-kor")
    }
}
