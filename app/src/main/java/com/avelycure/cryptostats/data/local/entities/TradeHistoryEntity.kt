package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avelycure.cryptostats.domain.Trade

@Entity(tableName = "trade_history")
data class TradeHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dataOfSave: Long,
    val timestampms: Long,
    val tid: Long,
    val price: Float,
    val amount: Float,
    val type: String
)

fun TradeHistoryEntity.toTrade(): Trade {
    return Trade(
        timestampms = timestampms,
        tid = tid,
        price = price,
        amount = amount,
        type = type
    )
}