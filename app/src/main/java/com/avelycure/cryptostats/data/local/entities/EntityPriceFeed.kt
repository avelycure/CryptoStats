package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.domain.models.CoinPrice

@Entity(tableName = "price_feed")
data class EntityPriceFeed(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val pair: String,
    val price: String,
    val percentChange24h: String
)

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