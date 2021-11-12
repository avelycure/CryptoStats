package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.EntityPriceFeed
import com.avelycure.cryptostats.domain.models.CoinPrice

data class ResponsePriceFeed(
    val pair: String,
    val price: String,
    val percentChange24h: String
)

fun ResponsePriceFeed.toEntityPriceFeed(): EntityPriceFeed {
    return EntityPriceFeed(
        id = 0,
        dateOfSave = 0,
        pair = pair,
        price = price,
        percentChange24h = percentChange24h
    )
}

fun ResponsePriceFeed.toCoinPrice(): CoinPrice {
    return CoinPrice(
        price = price,
        percentChange24h = percentChange24h,
        pair = pair
    )
}
