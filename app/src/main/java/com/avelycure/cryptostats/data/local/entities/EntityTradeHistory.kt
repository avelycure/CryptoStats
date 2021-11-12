package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_history")
data class EntityTradeHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dataOfSave: Long,
    val timestampms: Long,
    val tid: Long,
    val price: Float,
    val amount: Float,
    val type: String
)