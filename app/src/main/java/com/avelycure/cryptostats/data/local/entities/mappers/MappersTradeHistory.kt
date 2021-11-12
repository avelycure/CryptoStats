package com.avelycure.cryptostats.data.local.entities.mappers

import com.avelycure.cryptostats.data.local.entities.EntityTradeHistory
import com.avelycure.cryptostats.domain.models.Trade

fun EntityTradeHistory.toTrade(): Trade {
    return Trade(
        timestampms = timestampms,
        tid = tid,
        price = price,
        amount = amount,
        type = type
    )
}