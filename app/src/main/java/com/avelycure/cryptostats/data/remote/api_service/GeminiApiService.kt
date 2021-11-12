package com.avelycure.cryptostats.data.remote.api_service

import com.avelycure.cryptostats.data.remote.models.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApiService {
    @GET("v1/auction/{symbol}/history")
    fun getAuctionHistory(@Path("symbol") symbol: String): Observable<List<ResponseAuctionHistory>>

    @GET("v1/trades/{symbol}")
    fun getTradeHistory(
        @Path("symbol") symbol: String,
        @Query("limit_trades") tradesLimit: Int
    ): Observable<List<ResponseTradeHistory>>

    @GET("v2/candles/{symbol}/{time_frame}")
    fun getCandles(
        @Path("symbol") symbol: String,
        @Path("time_frame") time_frame: String
    ): Observable<List<List<Float>>>

    @GET("v2/ticker/{symbol}")
    fun getTickerV2(@Path("symbol") symbol: String): Observable<ResponseTickerV2>

    @GET("v1/pubticker/{symbol}")
    fun getTickerV1(@Path("symbol") symbol: String): Observable<ResponseTickerV1>

    @GET("v1/pricefeed")
    fun getPriceFeed(): Observable<List<ResponsePriceFeed>>
}