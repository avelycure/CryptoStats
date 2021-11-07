package com.avelycure.cryptostats.domain

data class Trade(
    val timestampms: Long,
    val tid: Long,
    val price: Float,
    val amount: Float,
    val type: String
)