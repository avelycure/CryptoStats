package com.avelycure.cryptostats.data.remote.models

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