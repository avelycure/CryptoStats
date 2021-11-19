package com.avelycure.cryptostats.di

import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import org.koin.dsl.module

val appModule = module {
    single<ICryptoRepo> {
        CryptoRepo(
            apiService = get(),
            cacheDao = get()
        )
    }
}