package com.avelycure.cryptostats.data.local.entities.mappers

import com.avelycure.cryptostats.data.local.entities.EntityCandles
import com.avelycure.cryptostats.data.local.entities.EntitySmallCandle
import com.avelycure.cryptostats.domain.models.Candle
import kotlin.math.roundToInt

fun List<List<Float>>.toCandleList(): List<Candle> {
    val candleList = mutableListOf<Candle>()
    for (candle in this)
        candleList.add(
            Candle(
                time = candle[0],
                open = candle[1],
                high = candle[2],
                low = candle[3],
                close = candle[4]
            )
        )
    candleList.sortBy { it.time }
    return candleList
}

fun List<Float>.toSmallCandle(candleFK: Int): EntitySmallCandle {
    return EntitySmallCandle(
        id = 0,
        candleFK = candleFK,
        time = this[0],
        open = this[1],
        high = this[2],
        low = this[3],
        close = this[4],
    )
}

fun List<List<Float>>.toEntityCandles(): EntityCandles {
    return EntityCandles(
        id = 0,
        dateOfSave = 0L
    )
}

fun EntityCandles.toCandleList(): List<Candle> {
    return this.candles.map { smallCandle ->
        Candle(
            time = smallCandle.time,
            open = smallCandle.open,
            high = smallCandle.high,
            low = smallCandle.low,
            close = smallCandle.close,
        )
    }.sortedBy { it.time }
}
