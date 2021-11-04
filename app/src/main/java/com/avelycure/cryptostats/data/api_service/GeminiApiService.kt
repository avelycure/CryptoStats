package com.avelycure.cryptostats.data.api_service

import com.avelycure.cryptostats.data.models.AuctionHistory
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApiService {
    @GET("v1/auction/{symbol}/history")
    fun getAuctionHistory(@Path("symbol") symbol: String): Observable<List<AuctionHistory>>

    @GET("v1/trades/{symbol}")
    fun getTradeHistory(
        @Path("symbol") symbol: String,
        @Query("limit_trades") tradesLimit: Int
    ): Observable<List<TradeHistory>>

    @GET("v2/candles/{symbol}/1day")
    fun getCandles(@Path("symbol") symbol: String): Observable<List<List<Float>>>

    @GET("v2/ticker/{symbol}")
    fun getTickerV2(@Path("symbol") symbol: String): Observable<TickerV2>

    @GET("v1/pubticker/{symbol}")
    fun getTickerV1(@Path("symbol") symbol: String): Observable<TickerV1>
}