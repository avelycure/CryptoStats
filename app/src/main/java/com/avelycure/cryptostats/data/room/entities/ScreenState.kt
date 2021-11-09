package com.avelycure.cryptostats.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Statistic24h
import com.avelycure.cryptostats.domain.Ticker
import com.avelycure.cryptostats.domain.Trade

@Entity(tableName = "screen_state")
data class ScreenState(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val high: Float
)