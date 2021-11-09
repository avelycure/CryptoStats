package com.avelycure.cryptostats.domain

import androidx.room.Entity

@Entity
data class CoinPrice(
    val price: String = "",
    val percentChange24h: String = ""
)
