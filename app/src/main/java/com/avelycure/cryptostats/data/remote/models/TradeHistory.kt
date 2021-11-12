package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.TradeHistoryEntity
import com.avelycure.cryptostats.domain.Trade

data class TradeHistory(
    val timestamp: Long,
    val timestampms: Long,
    val tid: Long,
    val price: Float,
    val amount: Float,
    val exchange: String,
    val type: String
)

fun TradeHistory.toTradeHistoryEntity(): TradeHistoryEntity {
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

fun TradeHistory.toTrade(): Trade {
    return Trade(
        timestampms = timestampms,
        tid = tid,
        price = price,
        amount = amount,
        type = type
    )
}
