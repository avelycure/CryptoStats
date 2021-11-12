package com.avelycure.cryptostats.domain.models

import com.avelycure.cryptostats.data.local.entities.EntityTickerV1

data class TickerV1(
    val bid: Float,
    val ask: Float
)