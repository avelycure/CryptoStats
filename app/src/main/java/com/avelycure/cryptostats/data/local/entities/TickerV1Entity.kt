package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.domain.TickerV1Model

@Entity(tableName = "ticker_v1")
data class TickerV1Entity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val bid: Float,
    val ask: Float,
)

fun TickerV1Entity.toTickerV1Model(): TickerV1Model {
    return TickerV1Model(
        bid = bid,
        ask = ask
    )
}