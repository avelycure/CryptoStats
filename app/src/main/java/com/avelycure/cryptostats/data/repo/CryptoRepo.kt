package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.api_service.GeminiApiService
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import com.avelycure.cryptostats.data.network.INetworkStatus
import com.avelycure.cryptostats.data.room.dao.ScreenDao
import com.avelycure.cryptostats.data.room.entities.ScreenState
import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Statistic24h
import com.avelycure.cryptostats.domain.Ticker
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class CryptoRepo(
    private val apiService: GeminiApiService,
    private val networkStatus: INetworkStatus,
    private val screenDao: ScreenDao
) : ICryptoRepo {

    override fun getCandles(symbol: String, timeFrame: String): Observable<List<List<Float>>> {
        return apiService.getCandles(symbol, timeFrame)
    }

    override fun getTickerV2(symbol: String): Observable<DataState<TickerV2>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService.getTickerV2(symbol)
                    .flatMap { tickerV2 ->
                        screenDao.insert(
                            ScreenState(
                                id = 0,
                                dateOfSave = 1,
                                high = tickerV2.high
                                /*statistic = Statistic24h("", 0f, 0f, 0f, emptyList(), emptyList()),
                                coinPrice = CoinPrice(),
                                ticker = Ticker(0f,0f),
                                trades = emptyList()*/
                            )
                        )
                        Observable.fromCallable { DataState.DataRemote(data = tickerV2) }
                    }.repeatWhen { completed ->
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = screenDao.getState().last()
                Observable.fromCallable {
                    DataState.DataCache(
                        data = TickerV2(
                            "${result.id}", 505.505f, result.high, 606.606f, 303.303f,
                            emptyList(), 101.101F, 202.202f
                        )
                    )
                }
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