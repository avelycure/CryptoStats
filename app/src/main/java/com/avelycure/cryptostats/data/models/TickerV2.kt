package com.avelycure.cryptostats.data.models

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
