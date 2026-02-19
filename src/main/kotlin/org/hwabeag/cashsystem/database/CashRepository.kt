package org.hwabeag.cashsystem.database

interface CashRepository {
    fun initializePlayer(playerName: String)
    fun getCash(playerName: String): Long
    fun setCash(playerName: String, amount: Long)
    fun addCash(playerName: String, amount: Long): Long
    fun takeCash(playerName: String, amount: Long): Long?
    fun close()
}

