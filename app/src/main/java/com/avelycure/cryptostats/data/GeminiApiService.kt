package com.avelycure.cryptostats.data

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface GeminiApiService {
    @GET("v1/auction/btcusd/history")
    fun getAuctionHistory(): Observable<List<AuctionHistoryResponse>>
}