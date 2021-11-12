package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.local.type_converters.ConverterListFloat
import com.avelycure.cryptostats.domain.models.TickerV2

@Entity(tableName = "ticker")
data class EntityTickerV2(
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
    @TypeConverters(ConverterListFloat::class)
    val changes: List<Float>
)

fun EntityTickerV2.toTickerV2(): TickerV2 {
    return TickerV2(
        symbol = symbol,
        bid = bid,
        ask = ask,
        high = high,
        open = open,
        close = close,
        low = low,
        changes = changes
    )
}