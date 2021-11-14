package com.avelycure.cryptostats.data.repo

import android.util.Log
import com.avelycure.cryptostats.data.remote.api_service.GeminiApiService
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import com.avelycure.cryptostats.data.local.dao.CacheDao
import com.avelycure.cryptostats.data.local.entities.EntityCandles
import com.avelycure.cryptostats.data.local.entities.EntityPriceFeed
import com.avelycure.cryptostats.data.local.entities.mappers.*
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV2
import com.avelycure.cryptostats.data.remote.models.mappers.*
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class CryptoRepo(
    private val apiService: GeminiApiService,
    private val networkStatus: INetworkStatus,
    private val cacheDao: CacheDao
) : ICryptoRepo {
    override fun getCandlesFromRemote(
        symbol: String,
        timeFrame: String
    ): Observable<List<List<Float>>> {
        return apiService
            .getCandles(symbol, timeFrame)
            .flatMap { candles ->
                cacheDao.insertCandles(candles.toEntityCandles())
                Observable.fromCallable { candles }
            }.repeatWhen { completed ->
                Log.d("mytag", "Repeated request candles")
                completed.delay(5, TimeUnit.MINUTES)
            }
    }

    override fun getCandlesFromCache(): Observable<EntityCandles> {
        return Observable.fromCallable { cacheDao.getCandles().last() }
    }

    override fun getTickerV2FromRemote(symbol: String): Observable<ResponseTickerV2> {
        return apiService
            .getTickerV2(symbol)
            .flatMap { tickerV2 ->
                cacheDao.insertTickerV2(tickerV2.toEntityTickerV2())
                Observable.fromCallable { tickerV2 }
            }.repeatWhen { completed ->
                Log.d("mytag", "Repeated request")
                completed.delay(5, TimeUnit.SECONDS)
            }
    }

    override fun getTickerV2FromCache() = cacheDao.getTickerV2().last()

    override fun getPriceFeed(): Observable<DataState<List<CoinPrice>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService
                    .getPriceFeed()
                    .flatMap { priceFeed ->
                        cacheDao.dropPriceFeedTable()
                        for (price in priceFeed)
                            cacheDao.insertPriceFeed(price.toEntityPriceFeed())
                        Observable.fromCallable { DataState.DataRemote(data = priceFeed.map { it.toCoinPrice() }) }
                    }.repeatWhen { completed ->
                        Log.d("mytag", "Repeated request")
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = cacheDao.getPriceFeed().map { it.toCoinPrice() }
                Observable.fromCallable { DataState.DataCache(data = result) }
            }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }

    override fun getPriceFeedFromRemote(): Observable<List<ResponsePriceFeed>> {
        return apiService
            .getPriceFeed()
            .flatMap { priceFeed ->
                cacheDao.dropPriceFeedTable()
                for (price in priceFeed)
                    cacheDao.insertPriceFeed(price.toEntityPriceFeed())
                Observable.fromCallable { priceFeed }
            }.repeatWhen { completed ->
                Log.d("mytag", "Repeated request")
                completed.delay(5, TimeUnit.SECONDS)
            }
    }

    override fun getPriceFeedFromCache():List<EntityPriceFeed>{
        return cacheDao.getPriceFeed()
    }

    override fun getTickerV1(symbol: String): Observable<DataState<TickerV1>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService
                    .getTickerV1(symbol)
                    .flatMap { tickerV1 ->
                        cacheDao.insertTickerV1(tickerV1.toEntityTickerV1())
                        Observable.fromCallable { DataState.DataRemote(data = tickerV1.toTickerV1()) }
                    }.repeatWhen { completed ->
                        Log.d("mytag", "Repeated request")
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = cacheDao.getTickerV1().last().toTickerV1()
                Observable.fromCallable { DataState.DataCache(data = result) }
            }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }

    override fun getTrades(symbol: String, limit: Int): Observable<DataState<List<Trade>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                apiService
                    .getTradeHistory(symbol, limit)
                    .flatMap { trades ->
                        cacheDao.dropTradeHistoryTable()
                        for (trade in trades)
                            cacheDao.insertTradeHistory(trade.toTradeHistoryEntity())
                        Observable.fromCallable { DataState.DataRemote(data = trades.map { it.toTrade() }) }
                    }.repeatWhen { completed ->
                        Log.d("mytag", "Repeated request")
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            } else {
                val result = cacheDao.getTradeHistory().map { it.toTrade() }
                Observable.fromCallable { DataState.DataCache(data = result) }
            }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }
}