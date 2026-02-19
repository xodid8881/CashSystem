package org.hwabeag.cashsystem.database

import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

class MySqlCashRepository(
    private val host: String,
    private val port: Int,
    private val database: String,
    private val username: String,
    private val password: String,
    private val useSsl: Boolean
) : CashRepository {

    private val url: String =
        "jdbc:mysql://$host:$port/$database?characterEncoding=utf8&serverTimezone=UTC&useSSL=$useSsl&allowPublicKeyRetrieval=true"

    init {
        openConnection().use { connection ->
            connection.createStatement().use { stmt ->
                stmt.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS cash_balances (
                        player_name VARCHAR(16) PRIMARY KEY,
                        cash BIGINT NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }
    }

    override fun initializePlayer(playerName: String) {
        openConnection().use { connection ->
            connection.prepareStatement(
                "INSERT INTO cash_balances(player_name, cash) VALUES (?, 0) ON DUPLICATE KEY UPDATE player_name = VALUES(player_name)"
            ).use { ps ->
                ps.setString(1, playerName)
                ps.executeUpdate()
            }
        }
    }

    override fun getCash(playerName: String): Long {
        openConnection().use { connection ->
            connection.prepareStatement("SELECT cash FROM cash_balances WHERE player_name = ?").use { ps ->
                ps.setString(1, playerName)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) rs.getLong("cash") else 0L
                }
            }
        }
    }

    override fun setCash(playerName: String, amount: Long) {
        val safeAmount = amount.coerceAtLeast(0)
        openConnection().use { connection ->
            connection.prepareStatement(
                "INSERT INTO cash_balances(player_name, cash) VALUES (?, ?) ON DUPLICATE KEY UPDATE cash = VALUES(cash)"
            ).use { ps ->
                ps.setString(1, playerName)
                ps.setLong(2, safeAmount)
                ps.executeUpdate()
            }
        }
    }

    override fun addCash(playerName: String, amount: Long): Long {
        if (amount <= 0L) return getCash(playerName)
        val result = getCash(playerName) + amount
        setCash(playerName, result)
        return result
    }

    override fun takeCash(playerName: String, amount: Long): Long? {
        if (amount <= 0L) return getCash(playerName)
        val current = getCash(playerName)
        if (current < amount) {
            return null
        }
        val result = current - amount
        setCash(playerName, result)
        return result
    }

    override fun close() {
    }

    private fun openConnection(): Connection {
        val properties = Properties().apply {
            put("user", username)
            put("password", password)
        }
        return DriverManager.getConnection(url, properties)
    }
}

