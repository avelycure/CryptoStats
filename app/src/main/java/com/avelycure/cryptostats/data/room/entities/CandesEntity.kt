package com.avelycure.cryptostats.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CandesEntity (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val dateOfSave: Long,
    val time: Float,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)