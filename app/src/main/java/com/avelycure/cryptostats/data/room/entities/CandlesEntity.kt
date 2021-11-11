package com.avelycure.cryptostats.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CandlesEntity (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val dateOfSave: Long,
    val candles: List<List<Float>>
)