package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_feed")
data class EntityPriceFeed(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val pair: String,
    val price: String,
    val percentChange24h: String
)