package com.avelycure.cryptostats.domain

import androidx.room.Entity

@Entity
data class Ticker(
    val bid: Float,
    val ask: Float
)
