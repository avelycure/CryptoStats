package com.avelycure.cryptostats.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.room.dao.ScreenDao
import com.avelycure.cryptostats.data.room.entities.*
import com.avelycure.cryptostats.data.room.type_converters.ConverterListFloat
import com.avelycure.cryptostats.data.room.type_converters.ConverterListListFloat

@Database(
    entities = arrayOf(
        TickerEntity::class,
        PriceFeedEntity::class,
        TickerV1Entity::class,
        TradeHistoryEntity::class,
        CandlesEntity::class
    ), version = 1
)
@TypeConverters(
    value = arrayOf(
        ConverterListFloat::class,
        ConverterListListFloat::class
    )
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenDao(): ScreenDao
}