package com.avelycure.cryptostats.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.avelycure.cryptostats.data.room.entities.PriceFeedEntity
import com.avelycure.cryptostats.data.room.entities.TickerEntity
import com.avelycure.cryptostats.data.room.entities.TickerV1Entity
import com.avelycure.cryptostats.data.room.entities.TradeHistoryEntity

@Dao
interface ScreenDao {
    @Insert
    fun insertTicker(ticker: TickerEntity)

    @Query("SELECT * FROM ticker")
    fun getTicker(): List<TickerEntity>

    @Insert
    fun insertTickerV1(tickerV1: TickerV1Entity)

    @Query("SELECT * FROM ticker_v1")
    fun getTickerV1(): List<TickerV1Entity>

    @Insert
    fun insertTradeHistory(trade: TradeHistoryEntity)

    @Query("DELETE FROM trade_history")
    fun dropTradeHistoryTable()

    @Query("SELECT * FROM trade_history")
    fun getTradeHistory(): List<TradeHistoryEntity>

    @Insert
    fun insertPriceFeed(priceFeed: PriceFeedEntity)

    @Query("SELECT * FROM price_feed")
    fun getPriceFeed(): List<PriceFeedEntity>

    @Query("DELETE FROM price_feed")
    fun dropPriceFeedTable()
}