package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.api_service.GeminiApiService
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import io.reactivex.rxjava3.core.Observable

class CryptoRepo(
    private val apiService: GeminiApiService
): ICryptoRepo {

    override fun getCandles(symbol: String, timeFrame: String): Observable<List<List<Float>>> {
        return apiService.getCandles(symbol, timeFrame)
    }

    override fun getTickerV2(symbol: String): Observable<TickerV2> {
        return apiService.getTickerV2(symbol)
    }

    override fun getPriceFeed(): Observable<List<PriceFeed>> {
        return apiService.getPriceFeed()
    }

    override fun getTickerV1(symbol: String): Observable<TickerV1> {
        return apiService.getTickerV1(symbol)
    }

    override fun getTrades(symbol: String, limit: Int): Observable<List<TradeHistory>> {
        return apiService.getTradeHistory(symbol, limit)
    }
}