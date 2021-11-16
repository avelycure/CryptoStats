package com.avelycure.cryptostats.data.repo

import android.util.Log
import com.avelycure.cryptostats.data.remote.api_service.GeminiApiService
import com.avelycure.cryptostats.data.local.dao.CacheDao
import com.avelycure.cryptostats.data.local.entities.EntityCandles
import com.avelycure.cryptostats.data.local.entities.EntitySmallCandle
import com.avelycure.cryptostats.data.local.entities.mappers.*
import com.avelycure.cryptostats.data.remote.models.ResponsePriceFeed
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV1
import com.avelycure.cryptostats.data.remote.models.ResponseTickerV2
import com.avelycure.cryptostats.data.remote.models.ResponseTradeHistory
import com.avelycure.cryptostats.data.remote.models.mappers.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class CryptoRepo(
    private val apiService: GeminiApiService,
    private val cacheDao: CacheDao
) : ICryptoRepo {
    override fun getCandlesFromCache(): EntityCandles {
        val candle = cacheDao.getCandles()?.last()
        if (candle != null) {
            candle.candles = cacheDao.getSmallCandles()
            Log.d("mytag", "candles size: " + candle.candles.size)
            return candle
        }
        return EntityCandles()
    }

    override fun getTickerV2FromCache() = cacheDao.getTickerV2().last()
    override fun getPriceFeedFromCache() = cacheDao.getPriceFeed()
    override fun getTradesFromCache() = cacheDao.getTradeHistory()
    override fun getTickerV1FromCache() = cacheDao.getTickerV1().last()

    override fun getCandlesFromRemote(
        symbol: String,
        timeFrame: String
    ): Observable<List<List<Float>>> {
        return apiService
            .getCandles(symbol, timeFrame)
            .flatMap { candles ->
                //thread {
                try {
                    Log.d("mytag", "length of candles: ${candles.size}")
                    cacheDao.insertCandles(candles)
                } catch (e: Exception) {
                    Log.d("mytag", "i got error in candles: ${e.message}")
                }//}
                Observable.fromCallable { candles }
            }.repeatWhen { completed ->
                Log.d("mytag", "Repeated request candles")
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
                Log.d("mytag", "Repeated request tickerv2")
                completed.delay(10, TimeUnit.SECONDS)
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
                Log.d("mytag", "Repeated request price feed")
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
                Log.d("mytag", "Repeated request ticker v1")
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
                cacheDao.dropTradeHistoryTable()
                for (trade in trades)
                    cacheDao.insertTradeHistory(trade.toTradeHistoryEntity())
                Observable.fromCallable { trades }
            }.repeatWhen { completed ->
                Log.d("mytag", "Repeated request trade history")
                completed.delay(30, TimeUnit.SECONDS)
            }
    }
}