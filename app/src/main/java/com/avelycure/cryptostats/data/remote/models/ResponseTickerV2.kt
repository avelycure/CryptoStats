package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.EntityTickerV2
import com.avelycure.cryptostats.domain.models.TickerV2

data class ResponseTickerV2(
    val symbol: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val changes: List<Float>,
    val bid: Float,
    val ask: Float
)

fun ResponseTickerV2.toEntityTickerV2(): EntityTickerV2 {
    return EntityTickerV2(
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

fun ResponseTickerV2.toTickerV2(): TickerV2 {
    return TickerV2(
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
