package com.avelycure.cryptostats.data.remote.models

data class ResponsePriceFeed(
    val pair: String,
    val price: String,
    val percentChange24h: String
)