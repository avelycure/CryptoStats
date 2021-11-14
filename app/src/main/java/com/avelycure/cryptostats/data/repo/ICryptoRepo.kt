package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.local.entities.*
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV1
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV2
import com.avelycure.cryptostats.data.remote.models.ResponseTradeHistory
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable

interface ICryptoRepo {
    fun getCandlesFromRemote(symbol: String, timeFrame: String): Observable<List<List<Float>>>

    fun getCandlesFromCache(): EntityCandles

    fun getPriceFeedFromRemote(): Observable<List<ResponsePriceFeed>>

    fun getPriceFeedFromCache(): List<EntityPriceFeed>

    fun getTradesFromCache(): List<EntityTradeHistory>

    fun getTradesFromRemote(symbol: String, limit: Int): Observable<List<ResponseTradeHistory>>

    fun getTickerV1FromRemote(symbol: String): Observable<ResponseTickerV1>

    fun getTickerV1FromCache(): EntityTickerV1

    fun getTickerV2FromRemote(symbol: String): Observable<ResponseTickerV2>

    fun getTickerV2FromCache(): EntityTickerV2
}