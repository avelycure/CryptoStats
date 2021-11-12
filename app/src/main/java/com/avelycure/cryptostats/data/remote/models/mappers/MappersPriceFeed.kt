package com.avelycure.cryptostats.data.remote.models.mappers

import com.avelycure.cryptostats.data.local.entities.EntityPriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.domain.models.CoinPrice

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