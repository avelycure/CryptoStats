package com.avelycure.cryptostats.domain

import androidx.room.Entity

@Entity
data class Statistic24h(
    val symbol: String,
    val high: Float,
    val low: Float,
    val open: Float,
    val changes: List<Point>,
    val candles: List<Candle>
)
