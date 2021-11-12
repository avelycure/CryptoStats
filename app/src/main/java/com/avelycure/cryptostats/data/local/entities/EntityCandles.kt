package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.local.type_converters.ConverterListListFloat

@Entity(tableName = "candles")
data class EntityCandles(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    @TypeConverters(ConverterListListFloat::class)
    val candles: List<List<Float>>
)