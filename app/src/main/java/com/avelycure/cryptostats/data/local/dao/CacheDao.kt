package com.avelycure.cryptostats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.avelycure.cryptostats.data.local.entities.*
import com.avelycure.cryptostats.data.local.entities.mappers.toEntityCandles
import com.avelycure.cryptostats.data.local.entities.mappers.toSmallCandle
import io.reactivex.rxjava3.core.Completable

//todo actually it is not returning null it returns empty list
@Dao
interface CacheDao {

    @Insert
    fun insertTickerV1(tickerV1: EntityTickerV1)

    @Query("SELECT * FROM ticker_v1")
    fun getTickerV1(): List<EntityTickerV1>

    @Insert
    fun insertTickerV2(ticker: EntityTickerV2)

    @Query("SELECT * FROM ticker")
    fun getTickerV2(): List<EntityTickerV2>


    @Insert
    fun insertTradeHistory(trade: EntityTradeHistory)

    @Query("DELETE FROM trade_history")
    fun dropTradeHistoryTable()

    @Query("SELECT * FROM trade_history")
    fun getTradeHistory(): List<EntityTradeHistory>


    @Insert
    fun insertPriceFeed(priceFeed: EntityPriceFeed)

    @Query("SELECT * FROM price_feed")
    fun getPriceFeed(): List<EntityPriceFeed>

    @Query("DELETE FROM price_feed")
    fun dropPriceFeedTable()


    @Insert
    fun insertCandles(candle: EntityCandles)

    @Insert
    fun insertSmallCandles(candle: EntitySmallCandle)

    @Query("SELECT * FROM candles")
    fun getCandles(): List<EntityCandles>

    @Query("SELECT * FROM small_candles")
    fun getSmallCandles(): List<EntitySmallCandle>

    @Query("DELETE FROM candles")
    fun dropCandlesTable()
}