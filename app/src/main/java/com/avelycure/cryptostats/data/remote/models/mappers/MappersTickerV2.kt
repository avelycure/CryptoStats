package com.avelycure.cryptostats.data.remote.models.mappers

import com.avelycure.cryptostats.data.local.entities.EntityTickerV2
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV2
import com.avelycure.cryptostats.domain.models.TickerV2

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
