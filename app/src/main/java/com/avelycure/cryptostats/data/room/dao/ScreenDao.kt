package com.avelycure.cryptostats.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.avelycure.cryptostats.data.room.entities.ScreenState

@Dao
interface ScreenDao {
    @Query("SELECT * FROM screen_state")
    fun getState(): List<ScreenState>

    @Insert
    fun insert(item: ScreenState)
}