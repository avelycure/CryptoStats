package com.avelycure.cryptostats.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.avelycure.cryptostats.data.room.entities.PriceFeedEntity
import com.avelycure.cryptostats.data.room.entities.TickerEntity

@Dao
interface ScreenDao {
    @Insert
    fun insertTicker(ticker: TickerEntity)

    @Insert
    fun insertPriceFeed(priceFeed: PriceFeedEntity)

    @Query("SELECT * FROM price_feed")
    fun getPriceFeed(): List<PriceFeedEntity>

    @Query("DELETE FROM price_feed")
    fun dropPriceFeedTable()

    @Query("SELECT * FROM ticker")
    fun getTicker(): List<TickerEntity>
}