package com.avelycure.cryptostats.di

import com.avelycure.cryptostats.domain.interactors.*
import org.koin.dsl.module

val interactorsModule = module {
    single<GetCandles> { GetCandles(get(), get()) }
    single<GetTickerV2> { GetTickerV2(get(), get()) }
    single<GetCoinPrice> { GetCoinPrice(get(), get()) }
    single<GetTickerV1> { GetTickerV1(get(), get()) }
    single<GetTrades> { GetTrades(get(), get()) }
    single<PrepareCandles> { PrepareCandles() }

    single<HomeInteractors> {
        HomeInteractors(
            getCandles = get(),
            getTickerV2 = get(),
            getCoinPrice = get(),
            getTickerV1 = get(),
            getTrades = get(),
            prepareCandles = get()
        )
    }
}