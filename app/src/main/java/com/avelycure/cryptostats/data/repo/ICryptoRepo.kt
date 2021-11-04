package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.models.TickerV2
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandles(symbol: String): Observable<List<List<Float>>>

    fun getTicker(symbol: String): Observable<TickerV2>
}