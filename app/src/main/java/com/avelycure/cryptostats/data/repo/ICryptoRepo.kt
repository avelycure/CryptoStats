package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ICryptoRepo {
    fun getCandles(symbol: String, timeFrame: String): Observable<List<List<Float>>>

    fun getTickerV2(symbol: String): Observable<TickerV2>

    fun getPriceFeed(): Single<List<PriceFeed>>

    fun getTickerV1(symbol: String):Observable<TickerV1>

    fun getTrades(symbol: String, limit: Int):Observable<List<TradeHistory>>
}