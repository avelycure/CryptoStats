package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.local.entities.EntityCandles
import com.avelycure.cryptostats.data.local.entities.EntityPriceFeed
import com.avelycure.cryptostats.data.local.entities.EntityTickerV2
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV2
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandlesFromRemote(symbol: String, timeFrame: String): Observable<List<List<Float>>>

    fun  getCandlesFromCache(): Observable<EntityCandles>

    fun getPriceFeed(): Observable<DataState<List<CoinPrice>>>

    fun getPriceFeedFromRemote(): Observable<List<ResponsePriceFeed>>

    fun getPriceFeedFromCache():List<EntityPriceFeed>

    fun getTickerV1(symbol: String): Observable<DataState<TickerV1>>

    fun getTickerV2FromRemote(symbol: String): Observable<ResponseTickerV2>

    fun getTickerV2FromCache(): EntityTickerV2

    fun getTrades(symbol: String, limit: Int): Observable<DataState<List<Trade>>>
}