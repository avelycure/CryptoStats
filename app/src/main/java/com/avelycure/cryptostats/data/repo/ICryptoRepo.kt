package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import com.avelycure.cryptostats.domain.Candle
import com.avelycure.cryptostats.domain.Ticker
import com.avelycure.cryptostats.domain.TickerV1Model
import com.avelycure.cryptostats.domain.Trade
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandles(symbol: String, timeFrame: String): Observable<DataState<List<Candle>>>

    fun getTicker(symbol: String): Observable<DataState<Ticker>>

    fun getPriceFeed(): Observable<DataState<List<PriceFeed>>>

    fun getTickerV1(symbol: String): Observable<DataState<TickerV1Model>>

    fun getTrades(symbol: String, limit: Int): Observable<DataState<List<Trade>>>
}