package com.avelycure.cryptostats.data

data class TradeHistory(
    val timestamp: Long,
    val timestampms: Long,
    val tid: Long,
    val amount: Float,
    val exchange: String,
    val type: String
)
