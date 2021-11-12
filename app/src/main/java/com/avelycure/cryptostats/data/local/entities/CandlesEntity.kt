package com.avelycure.cryptostats.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.local.type_converters.ConverterListListFloat
import com.avelycure.cryptostats.domain.models.Candle
import kotlin.math.roundToInt

@Entity(tableName = "candles")
data class CandlesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dateOfSave: Long,
    @TypeConverters(ConverterListListFloat::class)
    val candles: List<List<Float>>
)

fun List<List<Float>>.toCandleList(): List<Candle> {
    val candleList = mutableListOf<Candle>()
    for (candle in this)
        candleList.add(
            Candle(
                time = (candle[0] / 1000).roundToInt().toFloat(),
                open = candle[1],
                high = candle[2],
                low = candle[3],
                close = candle[4],

                )
        )
    candleList.sortBy { it.time }
    return candleList
}

fun List<List<Float>>.toCandlesEntity(): CandlesEntity {
    return CandlesEntity(
        id = 0,
        dateOfSave = 0L,
        candles = this.toList()
        //todo think about creating new list
    )
}

fun CandlesEntity.toCandleList(): List<Candle> {
    val candleList = mutableListOf<Candle>()
    for (candle in this.candles)
        candleList.add(
            Candle(
                time = (candle[0] / 1000).roundToInt().toFloat(),
                open = candle[1],
                high = candle[2],
                low = candle[3],
                close = candle[4],

                )
        )
    candleList.sortBy { it.time }
    return candleList
}
