package com.avelycure.cryptostats.di

import android.content.Context
import androidx.room.Room
import com.avelycure.cryptostats.data.local.AppDatabase
import org.koin.dsl.module

val cacheModule = module {
    fun provideAppDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "database").build()

    fun provideCacheDao(appDatabase: AppDatabase) = appDatabase.cacheDao()

    single { provideAppDatabase(get()) }

    single { provideCacheDao(get()) }
}