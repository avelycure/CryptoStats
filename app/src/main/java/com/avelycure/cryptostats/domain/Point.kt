package com.avelycure.cryptostats.domain

import androidx.room.Entity

@Entity
data class Point(
    val x: Float,
    val y: Float
)
