package com.avelycure.cryptostats.domain.interactors

import com.avelycure.cryptostats.domain.models.Candle
import io.reactivex.rxjava3.core.Observable

class PrepareCandles() {

    fun execute(data: List<Candle>): Observable<List<Candle>> {

        val last = data.last().time

        val dataForChartCopy = arrayListOf<Candle>()
        for (i in 0 until data.size)
            if (i % 2 == 0)
                dataForChartCopy.add(data[i].copy(time = ((data[i].time - last) / 1000F / 360F)))

        return Observable.fromCallable { dataForChartCopy }
    }
}