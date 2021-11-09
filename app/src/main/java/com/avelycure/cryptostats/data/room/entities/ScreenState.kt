package com.avelycure.cryptostats.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Statistic24h
import com.avelycure.cryptostats.domain.Ticker
import com.avelycure.cryptostats.domain.Trade

@Entity
data class ScreenState(
    @PrimaryKey
    val id: Int,
    val dateOfSave: Long,
    val statistic: Statistic24h,
    val coinPrice: CoinPrice,
    val ticker: Ticker,
    val trades: List<Trade>
)