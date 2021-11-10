package com.avelycure.cryptostats.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.avelycure.cryptostats.data.room.entities.TickerEntity

@Dao
interface ScreenDao {
    @Insert
    fun insertTicker(ticker: TickerEntity)

    @Query("SELECT * FROM ticker")
    fun getTicker(): List<TickerEntity>
}