package com.avelycure.cryptostats.domain.models

import com.avelycure.cryptostats.data.local.entities.TickerV1Entity

data class TickerV1Model(
    val bid: Float,
    val ask: Float
)

fun TickerV1Model.toTickerV1Entity():TickerV1Entity{
    return TickerV1Entity(
        bid = bid,
        ask = ask,
        id = 0,
        dateOfSave = 0L
    )
}