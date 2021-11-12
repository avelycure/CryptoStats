package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticker_v1")
data class EntityTickerV1(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    val bid: Float,
    val ask: Float,
)