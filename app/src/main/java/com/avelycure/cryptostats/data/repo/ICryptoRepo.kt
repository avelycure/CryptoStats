package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandles(symbol: String, timeFrame: String): Observable<List<List<Float>>>

    fun getTickerV2(symbol: String): Observable<DataState<TickerV2>>

    fun getPriceFeed(): Observable<List<PriceFeed>>

    fun getTickerV1(symbol: String):Observable<TickerV1>

    fun getTrades(symbol: String, limit: Int):Observable<List<TradeHistory>>
}