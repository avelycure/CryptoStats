package com.avelycure.cryptostats.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avelycure.cryptostats.data.room.dao.ScreenDao
import com.avelycure.cryptostats.data.room.entities.ScreenState

@Database(entities = arrayOf(ScreenState::class), version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun screenDao(): ScreenDao
}