package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.PriceFeedEntity

data class PriceFeed(
    val pair: String,
    val price: String,
    val percentChange24h: String
)

fun PriceFeed.toPriceFeedEntity():PriceFeedEntity{
    return PriceFeedEntity(
        id = 0,
        dateOfSave = 0,
        pair = pair,
        price = price,
        percentChange24h = percentChange24h
    )
}
