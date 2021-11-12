package com.avelycure.cryptostats.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avelycure.cryptostats.data.local.dao.CacheDao
import com.avelycure.cryptostats.data.local.entities.*
import com.avelycure.cryptostats.data.local.type_converters.ConverterListFloat
import com.avelycure.cryptostats.data.local.type_converters.ConverterListListFloat

@Database(
    entities = arrayOf(
        EntityTickerV2::class,
        EntityPriceFeed::class,
        EntityTickerV1::class,
        EntityTradeHistory::class,
        EntityCandles::class
    ), version = 1
)
@TypeConverters(
    value = arrayOf(
        ConverterListFloat::class,
        ConverterListListFloat::class
    )
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}