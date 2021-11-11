package com.avelycure.cryptostats.data.repo

import android.util.Log
import com.avelycure.cryptostats.data.api_service.GeminiApiService
import com.avelycure.cryptostats.data.models.*
import com.avelycure.cryptostats.data.network.INetworkStatus
import com.avelycure.cryptostats.data.room.dao.ScreenDao
import com.avelycure.cryptostats.data.room.entities.toPriceFeed
import com.avelycure.cryptostats.data.room.entities.toTicker
import com.avelycure.cryptostats.data.room.entities.toTickerV1Model
import com.avelycure.cryptostats.domain.Ticker
import com.avelycure.cryptostats.domain.TickerV1Model
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.UIComponent
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

    override fun getTicker(symbol: String): Observable<DataState<Ticker>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService
                    .getTickerV2(symbol)
                    .flatMap { tickerV2 ->
                        screenDao.insertTicker(tickerV2.toTickerEntity())
                        Observable.fromCallable { DataState.DataRemote(data = tickerV2.toTicker()) }
                    }.repeatWhen { completed ->
                        Log.d("mytag", "Repeated request")
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = screenDao.getTicker().last().toTicker()
                Observable.fromCallable { DataState.DataCache(data = result) }
            }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }

    override fun getPriceFeed(): Observable<DataState<List<PriceFeed>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService
                    .getPriceFeed()
                    .flatMap { priceFeed ->
                        screenDao.dropPriceFeedTable()
                        for (price in priceFeed)
                            screenDao.insertPriceFeed(price.toPriceFeedEntity())
                        Observable.fromCallable { DataState.DataRemote(data = priceFeed) }
                    }.repeatWhen { completed ->
                        Log.d("mytag", "Repeated request")
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = screenDao.getPriceFeed().map { it.toPriceFeed() }
                Observable.fromCallable { DataState.DataCache(data = result) }
            }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }

    override fun getTickerV1(symbol: String): Observable<DataState<TickerV1Model>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService
                    .getTickerV1(symbol)
                    .flatMap { tickerV1 ->
                        screenDao.insertTickerV1(tickerV1.toTickerV1Entity())
                        Observable.fromCallable { DataState.DataRemote(data = tickerV1.toTickerV1Model()) }
                    }.repeatWhen { completed ->
                        Log.d("mytag", "Repeated request")
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = screenDao.getTickerV1().last().toTickerV1Model()
                Observable.fromCallable { DataState.DataCache(data = result) }
            }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }

    override fun getTrades(symbol: String, limit: Int): Observable<List<TradeHistory>> {
        return apiService.getTradeHistory(symbol, limit)
    }
}