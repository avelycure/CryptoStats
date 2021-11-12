package com.avelycure.cryptostats.data.local.entities.mappers

import com.avelycure.cryptostats.data.local.entities.EntityCandles
import com.avelycure.cryptostats.domain.models.Candle
import kotlin.math.roundToInt

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

fun List<List<Float>>.toEntityCandles(): EntityCandles {
    return EntityCandles(
        id = 0,
        dateOfSave = 0L,
        candles = this.toList()
        //todo think about creating new list
    )
}

fun EntityCandles.toCandleList(): List<Candle> {
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
