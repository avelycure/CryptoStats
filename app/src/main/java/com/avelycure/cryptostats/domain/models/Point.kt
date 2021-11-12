package com.avelycure.cryptostats.domain.models

import androidx.room.Entity

@Entity
data class Point(
    val x: Float,
    val y: Float
)
