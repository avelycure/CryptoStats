package com.avelycure.cryptostats.data.local.entities.mappers

import com.avelycure.cryptostats.data.local.entities.EntityPriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.domain.models.CoinPrice

fun EntityPriceFeed.toPriceFeed(): ResponsePriceFeed {
    return ResponsePriceFeed(
        pair = pair,
        price = price,
        percentChange24h = percentChange24h
    )
}

fun EntityPriceFeed.toCoinPrice(): CoinPrice {
    return CoinPrice(
        price = price,
        percentChange24h = percentChange24h,
        pair = pair
    )
}