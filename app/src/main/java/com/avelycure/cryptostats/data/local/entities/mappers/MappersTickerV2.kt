package com.avelycure.cryptostats.data.local.entities.mappers

import com.avelycure.cryptostats.data.local.entities.EntityTickerV2
import com.avelycure.cryptostats.domain.models.TickerV2

fun EntityTickerV2.toTickerV2(): TickerV2 {
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