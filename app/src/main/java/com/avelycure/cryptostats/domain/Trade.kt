package com.avelycure.cryptostats.domain

import com.avelycure.cryptostats.data.room.entities.TradeHistoryEntity

data class Trade(
    val timestampms: Long,
    val tid: Long,
    val price: Float,
    val amount: Float,
    val type: String
)

fun Trade.toTradeHistoryEntity(): TradeHistoryEntity {
    return TradeHistoryEntity(
        id = 0,
        dataOfSave = 0L,
        timestampms = timestampms,
        tid = tid,
        price = price,
        amount = amount,
        type = type
    )
}