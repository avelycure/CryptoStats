package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.TickerEntity
import com.avelycure.cryptostats.domain.models.Ticker

data class TickerV2(
    val symbol: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val changes: List<Float>,
    val bid: Float,
    val ask: Float
)

fun TickerV2.toTickerEntity(): TickerEntity {
    return TickerEntity(
        id = 0,
        dateOfSave = 0L,
        symbol = symbol,
        bid = bid,
        ask = ask,
        high = high,
        open = open,
        close = close,
        low = low,
        changes = changes
    )
}

fun TickerV2.toTicker(): Ticker {
    return Ticker(
        symbol = symbol,
        bid = bid,
        ask = ask,
        high = high,
        open = open,
        close = close,
        low = low,
        changes = changes
    )
}
