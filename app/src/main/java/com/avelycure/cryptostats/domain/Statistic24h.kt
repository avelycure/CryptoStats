package com.avelycure.cryptostats.domain

data class Statistic24h(
    val symbol: String = "",
    val high: Float = 0f,
    val low: Float = 0f,
    val open: Float = 0f,
    val changes: List<Point> = emptyList(),
    val candles: List<Candle> = emptyList()
)
