package com.avelycure.cryptostats.domain.models

data class CoinPrice(
    val price: String = "",
    val percentChange24h: String = "",
    val pair: String = ""
)
