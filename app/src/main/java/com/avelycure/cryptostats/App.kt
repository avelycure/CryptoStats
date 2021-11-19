package com.avelycure.cryptostats

import android.app.Application
import com.avelycure.cryptostats.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(
                appModule,
                cacheModule,
                interactorsModule,
                remoteModule,
                viewModelModule
            )
        }
    }
}