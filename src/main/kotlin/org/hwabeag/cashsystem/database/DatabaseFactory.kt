package org.hwabeag.cashsystem.database

import org.bukkit.configuration.file.FileConfiguration
import org.hwabeag.cashsystem.CashSystem
import java.io.File

object DatabaseFactory {
    fun create(plugin: CashSystem, config: FileConfiguration): CashRepository {
        val type = config.getString("database.type", "sqlite")!!.lowercase()
        return if (type == "mysql") {
            val host = config.getString("database.mysql.host", "127.0.0.1")!!
            val port = config.getInt("database.mysql.port", 3306)
            val database = config.getString("database.mysql.database", "cashsystem")!!
            val username = config.getString("database.mysql.username", "root")!!
            val password = config.getString("database.mysql.password", "change-me")!!
            val useSsl = config.getBoolean("database.mysql.use-ssl", false)
            MySqlCashRepository(host, port, database, username, password, useSsl)
        } else {
            val sqliteFileName = config.getString("database.sqlite.file", "cashsystem.db")!!
            val sqliteFile = File(plugin.dataFolder, sqliteFileName)
            SQLiteCashRepository(sqliteFile)
        }
    }
}

