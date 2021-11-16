package com.avelycure.cryptostats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.avelycure.cryptostats.data.local.entities.*
import io.reactivex.rxjava3.core.Completable

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
    fun insertCandles(candle: EntityCandles): Completable

    @Query("SELECT * FROM candles")
    fun getCandles(): List<EntityCandles>

    @Query("DELETE FROM candles")
    fun dropCandlesTable()
}