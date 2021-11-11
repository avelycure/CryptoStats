package com.avelycure.cryptostats.domain

data class Ticker(
    val symbol: String = "",
    val bid: Float = 0f,
    val ask: Float = 0f,
    val open: Float = 0f,
    val high: Float = 0f,
    val low: Float = 0f,
    val close: Float = 0f,
    val changes: List<Float> = emptyList(),
)
