package com.avelycure.cryptostats.data

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GeminiApiService {
    @GET("v1/auction/{symbol}/history")
    fun getAuctionHistory(@Path("symbol") symbol: String): Observable<List<AuctionHistoryResponse>>

    @GET("v1/trades/{symbol}?limit_trades=30")
    fun getTradeHistory(@Path("symbol") symbol: String): Observable<List<TradeHistory>>

    @GET("v2/candles/{symbol}/1day")
    fun getCandles(@Path("symbol") symbol: String): Observable<List<List<Float>>>
}