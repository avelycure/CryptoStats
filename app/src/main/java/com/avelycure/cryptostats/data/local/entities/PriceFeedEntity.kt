package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.data.remote.models.PriceFeed

@Entity(tableName = "price_feed")
data class PriceFeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val pair: String,
    val price: String,
    val percentChange24h: String
)

fun PriceFeedEntity.toPriceFeed():PriceFeed{
    return PriceFeed(
        pair = pair,
        price = price,
        percentChange24h = percentChange24h
    )
}