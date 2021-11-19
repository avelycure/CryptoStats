package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.local.type_converters.ConverterListListFloat

@Entity(tableName = "candles")
data class EntityCandles @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var dateOfSave: Long,
){
    @Ignore
    var candles: List<EntitySmallCandle> = emptyList()
}