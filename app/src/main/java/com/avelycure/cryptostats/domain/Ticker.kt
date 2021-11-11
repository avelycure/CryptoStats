package com.avelycure.cryptostats.domain

data class Ticker(
    val symbol: String,
    val bid: Float,
    val ask: Float,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val changes: List<Float>,
)
