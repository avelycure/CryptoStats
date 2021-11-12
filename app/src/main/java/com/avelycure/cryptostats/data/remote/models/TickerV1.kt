package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.TickerV1Entity
import com.avelycure.cryptostats.domain.models.TickerV1Model

data class TickerV1(
    val bid: Float,
    val ask: Float,
    val last: Float,
    val volume: VolumeBtcUsd
)

fun TickerV1.toTickerV1Entity(): TickerV1Entity{
    return TickerV1Entity(
        id = 0,
        dateOfSave = 0L,
        bid = bid,
        ask = ask
    )
}

fun TickerV1.toTickerV1Model(): TickerV1Model {
    return TickerV1Model(
        bid = bid,
        ask = ask
    )
}
