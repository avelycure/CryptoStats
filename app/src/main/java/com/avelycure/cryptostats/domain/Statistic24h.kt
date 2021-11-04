package com.avelycure.cryptostats.domain

data class Statistic24h(
    val symbol: String,
    val high: Float,
    val low: Float,
    val changes: List<Point>
)
