package com.avelycure.cryptostats.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.domain.Ticker

@Entity(tableName = "ticker")
data class TickerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val symbol: String,
    val bid: Float,
    val ask: Float,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
)

fun TickerEntity.toTicker(): Ticker {
    return Ticker(
        symbol = symbol,
        bid = bid,
        ask = ask,
        high = high,
        open = open,
        close = close,
        low = low,
        changes = emptyList()
    )
}
