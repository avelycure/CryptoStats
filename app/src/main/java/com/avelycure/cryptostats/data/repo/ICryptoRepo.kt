package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.local.entities.EntityCandles
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandles(symbol: String, timeFrame: String): Observable<DataState<List<Candle>>>

    fun getCandlesFromRemote(symbol: String, timeFrame: String): Observable<List<List<Float>>>

    fun  getCandlesFromCache(): Observable<EntityCandles>

    fun getPriceFeed(): Observable<DataState<List<CoinPrice>>>

    fun getTickerV1(symbol: String): Observable<DataState<TickerV1>>

    fun getTickerV2(symbol: String): Observable<DataState<TickerV2>>

    fun getTrades(symbol: String, limit: Int): Observable<DataState<List<Trade>>>
}