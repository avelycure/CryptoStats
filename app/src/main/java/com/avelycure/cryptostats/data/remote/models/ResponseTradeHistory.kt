package com.avelycure.cryptostats.data.remote.models

import com.avelycure.cryptostats.data.local.entities.EntityTradeHistory
import com.avelycure.cryptostats.domain.models.Trade

data class ResponseTradeHistory(
    val timestamp: Long,
    val timestampms: Long,
    val tid: Long,
    val price: Float,
    val amount: Float,
    val exchange: String,
    val type: String
)

fun ResponseTradeHistory.toTradeHistoryEntity(): EntityTradeHistory {
    return EntityTradeHistory(
        id = 0,
        dataOfSave = 0L,
        timestampms = timestampms,
        tid = tid,
        price = price,
        amount = amount,
        type = type
    )
}

fun ResponseTradeHistory.toTrade(): Trade {
    return Trade(
        timestampms = timestampms,
        tid = tid,
        price = price,
        amount = amount,
        type = type
    )
}
