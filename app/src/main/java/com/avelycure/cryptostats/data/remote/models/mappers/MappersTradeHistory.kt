package com.avelycure.cryptostats.data.remote.models.mappers

import com.avelycure.cryptostats.data.local.entities.EntityTradeHistory
import com.avelycure.cryptostats.data.remote.models.ResponseTradeHistory
import com.avelycure.cryptostats.domain.models.Trade

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