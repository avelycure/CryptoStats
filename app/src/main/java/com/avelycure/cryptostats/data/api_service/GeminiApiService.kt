package com.avelycure.cryptostats.data.api_service

import com.avelycure.cryptostats.data.models.AuctionHistory
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GeminiApiService {
    @GET("v1/auction/{symbol}/history")
    fun getAuctionHistory(@Path("symbol") symbol: String): Observable<List<AuctionHistory>>

    @GET("v1/trades/{symbol}?limit_trades=30")
    fun getTradeHistory(@Path("symbol") symbol: String): Observable<List<TradeHistory>>

    @GET("v2/candles/{symbol}/1day")
    fun getCandles(@Path("symbol") symbol: String): Observable<List<List<Float>>>

    @GET("v2/ticker/btcusd")
    fun getTickerV2(): Observable<TickerV2>
}