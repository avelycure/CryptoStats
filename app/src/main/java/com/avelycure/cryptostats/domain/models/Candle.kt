package com.avelycure.cryptostats.domain.models

data class Candle(
    val time: Float,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)