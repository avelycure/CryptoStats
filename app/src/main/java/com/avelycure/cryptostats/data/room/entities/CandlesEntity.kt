package com.avelycure.cryptostats.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.room.type_converters.ConverterListListFloat

@Entity
data class CandlesEntity (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val dateOfSave: Long,
    @TypeConverters(ConverterListListFloat::class)
    val candles: List<List<Float>>
)