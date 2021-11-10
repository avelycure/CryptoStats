package com.avelycure.cryptostats.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.room.dao.ScreenDao
import com.avelycure.cryptostats.data.room.entities.ScreenState
import com.avelycure.cryptostats.data.room.entities.TickerEntity
import com.avelycure.cryptostats.data.room.type_converters.ConverterListFloat

@Database(entities = arrayOf(ScreenState::class, TickerEntity::class), version = 1)
@TypeConverters(value = arrayOf(ConverterListFloat::class))
abstract class AppDatabase:RoomDatabase() {
    abstract fun screenDao(): ScreenDao
}