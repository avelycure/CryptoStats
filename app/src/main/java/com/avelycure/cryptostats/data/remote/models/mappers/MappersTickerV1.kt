package com.avelycure.cryptostats.data.remote.models.mappers

import com.avelycure.cryptostats.data.local.entities.EntityTickerV1
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV1
import com.avelycure.cryptostats.domain.models.TickerV1

fun ResponseTickerV1.toEntityTickerV1(): EntityTickerV1 {
    return EntityTickerV1(
        id = 0,
        dateOfSave = 0L,
        bid = bid,
        ask = ask
    )
}

fun ResponseTickerV1.toTickerV1(): TickerV1 {
    return TickerV1(
        bid = bid,
        ask = ask
    )
}