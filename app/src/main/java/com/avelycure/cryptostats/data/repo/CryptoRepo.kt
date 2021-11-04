package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.api_service.GeminiApiService
import com.avelycure.cryptostats.data.models.TickerV2
import io.reactivex.rxjava3.core.Observable

class CryptoRepo(
    private val apiService: GeminiApiService
): ICryptoRepo {

    override fun getCandles(symbol: String): Observable<List<List<Float>>> {
        return apiService.getCandles(symbol)
    }

    override fun getTicker(symbol: String): Observable<TickerV2> {
        return apiService.getTickerV2(symbol)
    }

}