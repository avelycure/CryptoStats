package com.avelycure.cryptostats.data.models

data class PriceFeed(
    val pair: String = "",
    val price: String = "",
    val percentChange24h: String = ""
)
