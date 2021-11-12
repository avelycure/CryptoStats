package com.avelycure.cryptostats.data.remote.models

data class ResponseCandle(
    val time: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
)
