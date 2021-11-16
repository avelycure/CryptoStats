package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "small_candles",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = EntityCandles::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("candleFK"),
            onDelete = CASCADE
        )
    )
)
data class EntitySmallCandle(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val candleFK: Int,
    val time: Float,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)