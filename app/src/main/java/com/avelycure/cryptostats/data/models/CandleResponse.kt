package com.avelycure.cryptostats.data.models

data class CandleResponse(
    val time: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
)