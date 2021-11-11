package com.avelycure.cryptostats.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.room.dao.ScreenDao
import com.avelycure.cryptostats.data.room.entities.PriceFeedEntity
import com.avelycure.cryptostats.data.room.entities.TickerEntity
import com.avelycure.cryptostats.data.room.entities.TickerV1Entity
import com.avelycure.cryptostats.data.room.entities.TradeHistoryEntity
import com.avelycure.cryptostats.data.room.type_converters.ConverterListFloat

@Database(
    entities = arrayOf(
        TickerEntity::class,
        PriceFeedEntity::class,
        TickerV1Entity::class,
        TradeHistoryEntity::class
    ), version = 1
)
@TypeConverters(value = arrayOf(ConverterListFloat::class))
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenDao(): ScreenDao
}