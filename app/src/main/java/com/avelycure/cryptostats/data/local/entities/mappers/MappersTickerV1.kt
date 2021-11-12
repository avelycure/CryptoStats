package com.avelycure.cryptostats.data.local.entities.mappers

import com.avelycure.cryptostats.data.local.entities.EntityTickerV1
import com.avelycure.cryptostats.domain.models.TickerV1

fun EntityTickerV1.toTickerV1(): TickerV1 {
    return TickerV1(
        bid = bid,
        ask = ask
    )
}