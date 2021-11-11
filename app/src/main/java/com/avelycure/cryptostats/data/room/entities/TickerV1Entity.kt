package com.avelycure.cryptostats.data.room.entities

import com.avelycure.cryptostats.domain.Ticker

data class TickerV1Entity(
    val bid: Float,
    val ask: Float,
    val last: Float,
    val BTC: Float,
    val USD: Float,
    val timestamp: Long
)

fun TickerV1Entity.toTicker(): Ticker{
    return Ticker(
        bid = bid,
        ask = ask,
        high = 0f,
        low = 0f,
        changes = emptyList(),
        close = 0f,
        symbol = "",
        open = 0f
    )
}