package com.avelycure.cryptostats.data.remote.models

data class ResponseTickerV1(
    val bid: Float,
    val ask: Float,
    val last: Float,
    val responseVolume: ResponseVolumeBtcUsd
)
