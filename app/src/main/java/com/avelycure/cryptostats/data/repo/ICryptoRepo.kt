package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.remote.models.PriceFeed
import com.avelycure.cryptostats.domain.models.Candle
import com.avelycure.cryptostats.domain.models.Ticker
import com.avelycure.cryptostats.domain.models.TickerV1Model
import com.avelycure.cryptostats.domain.models.Trade
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandles(symbol: String, timeFrame: String): Observable<DataState<List<Candle>>>

    fun getTicker(symbol: String): Observable<DataState<Ticker>>

    fun getPriceFeed(): Observable<DataState<List<PriceFeed>>>

    fun getTickerV1(symbol: String): Observable<DataState<TickerV1Model>>

    fun getTrades(symbol: String, limit: Int): Observable<DataState<List<Trade>>>
}