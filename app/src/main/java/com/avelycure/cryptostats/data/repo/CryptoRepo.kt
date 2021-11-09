package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.api_service.GeminiApiService
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import com.avelycure.cryptostats.data.network.INetworkStatus
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class CryptoRepo(
    private val apiService: GeminiApiService,
    private val networkStatus: INetworkStatus
) : ICryptoRepo {

    override fun getCandles(symbol: String, timeFrame: String): Observable<List<List<Float>>> {
        return apiService.getCandles(symbol, timeFrame)
    }

    override fun getTickerV2(symbol: String): Observable<DataState<TickerV2>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService.getTickerV2(symbol)
                    .flatMap { tickerV2 ->
                        Observable.fromCallable { DataState.DataRemote(data = tickerV2) }
                    }.repeatWhen { completed ->
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else
                Observable.fromCallable {
                    DataState.DataCache(
                        data = TickerV2(
                            "", 0f, 0f, 0f, 0f,
                            emptyList(), 0f, 0f
                        )
                    )
                }
        }.retryWhen { error ->
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
        }
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