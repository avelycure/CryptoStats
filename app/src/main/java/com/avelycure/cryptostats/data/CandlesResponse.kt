package com.avelycure.cryptostats.data

data class CandlesResponse(
    val time: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
)
