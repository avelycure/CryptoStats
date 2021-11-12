package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.domain.models.TickerV1

@Entity(tableName = "ticker_v1")
data class EntityTickerV1(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val bid: Float,
    val ask: Float,
)

fun EntityTickerV1.toTickerV1(): TickerV1 {
    return TickerV1(
        bid = bid,
        ask = ask
    )
}