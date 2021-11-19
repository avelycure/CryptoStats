package com.avelycure.cryptostats.data.repo

import android.util.Log
import com.avelycure.cryptostats.data.remote.api_service.GeminiApiService
import com.avelycure.cryptostats.data.local.dao.CacheDao
import com.avelycure.cryptostats.data.local.entities.*
import com.avelycure.cryptostats.data.local.entities.mappers.*
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV1
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV2
import com.avelycure.cryptostats.data.remote.models.ResponseTradeHistory
import com.avelycure.cryptostats.data.remote.models.mappers.*
import com.avelycure.cryptostats.utils.exceptions.EmptyCacheException
import io.reactivex.rxjava3.core.Observable
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class CryptoRepo(
    private val apiService: GeminiApiService,
    private val cacheDao: CacheDao
) : ICryptoRepo {
    override fun getCandlesFromCache(): EntityCandles {
        val candles = cacheDao.getCandles()
        if (candles.isEmpty())
            throw EmptyCacheException()

        val smallCandles = cacheDao.getSmallCandles()
        if (smallCandles.isEmpty())
            throw EmptyCacheException()

        val lastCandle = candles.last()
        lastCandle.candles = smallCandles
        return lastCandle
    }

    override fun getPriceFeedFromCache(): List<EntityPriceFeed> {
        val result = cacheDao.getPriceFeed()
        if (result.isEmpty())
            throw EmptyCacheException()
        return result
    }

    override fun getTickerV2FromCache(): EntityTickerV2 {
        val result = cacheDao.getTickerV2()
        if (result.isEmpty())
            throw EmptyCacheException()
        return result.last()
    }

    override fun getTradesFromCache(): List<EntityTradeHistory> {
        val result = cacheDao.getTradeHistory()
        if (result.isEmpty())
            throw EmptyCacheException()
        return result
    }

    override fun getTickerV1FromCache(): EntityTickerV1 {
        val result = cacheDao.getTickerV1()
        if (result.isEmpty())
            throw EmptyCacheException()
        return result.last()
    }

    override fun getCandlesFromRemote(
        symbol: String,
        timeFrame: String
    ): Observable<List<List<Float>>> {
        return apiService
            .getCandles(symbol, timeFrame)
            .flatMap { candles ->
                thread {
                    cacheDao.insertCandles(candles.toEntityCandles())
                    val candlesEntitiesList = cacheDao.getCandles()
                    if (candlesEntitiesList.isNotEmpty()) {
                        val candleFK = candlesEntitiesList.last().id
                        for (candle in candles)
                            cacheDao.insertSmallCandles(candle.toSmallCandle(candleFK))
                    }
                }
                Observable.fromCallable { candles }
            }.repeatWhen { completed ->
                completed.delay(5, TimeUnit.MINUTES)
            }
    }

    override fun getTickerV2FromRemote(symbol: String): Observable<ResponseTickerV2> {
        return apiService
            .getTickerV2(symbol)
            .flatMap { tickerV2 ->
                cacheDao.insertTickerV2(tickerV2.toEntityTickerV2())
                Observable.fromCallable { tickerV2 }
            }.repeatWhen { completed ->
                completed.delay(10, TimeUnit.SECONDS)
            }
    }

    override fun getPriceFeedFromRemote(): Observable<List<ResponsePriceFeed>> {
        return apiService
            .getPriceFeed()
            .flatMap { priceFeed ->
                thread {
                    cacheDao.dropPriceFeedTable()
                    for (price in priceFeed)
                        cacheDao.insertPriceFeed(price.toEntityPriceFeed())
                }
                Observable.fromCallable { priceFeed }
            }.repeatWhen { completed ->
                completed.delay(10, TimeUnit.SECONDS)
            }
    }

    override fun getTickerV1FromRemote(symbol: String): Observable<ResponseTickerV1> {
        return apiService
            .getTickerV1(symbol)
            .flatMap { tickerV1 ->
                cacheDao.insertTickerV1(tickerV1.toEntityTickerV1())
                Observable.fromCallable { tickerV1 }
            }.repeatWhen { completed ->
                completed.delay(10, TimeUnit.SECONDS)
            }
    }

    override fun getTradesFromRemote(
        symbol: String,
        limit: Int
    ): Observable<List<ResponseTradeHistory>> {
        return apiService
            .getTradeHistory(symbol, limit)
            .flatMap { trades ->
                thread {
                    cacheDao.dropTradeHistoryTable()
                    for (trade in trades)
                        cacheDao.insertTradeHistory(trade.toTradeHistoryEntity())
                }
                Observable.fromCallable { trades }
            }.repeatWhen { completed ->
                Log.d("mytag", "Repeated request trade history")
                completed.delay(30, TimeUnit.SECONDS)
            }
    }
}