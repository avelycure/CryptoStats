package com.avelycure.cryptostats.di

import android.content.Context
import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.remote.api_service.GeminiApiService
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import com.avelycure.cryptostats.utils.network_utils.NetworkStatus
import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.presentation.home.CryptoInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.avelycure.cryptostats.data.local.AppDatabase

import androidx.room.Room
import com.avelycure.cryptostats.domain.interactors.*
import com.avelycure.cryptostats.presentation.home.HomeInteractors

val appModule = module {
    single<ICryptoRepo> {
        CryptoRepo(
            apiService = get(),
            cacheDao = get()
        )
    }

    single<INetworkStatus> { NetworkStatus(get()) }

    fun provideAppDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "database").build()

    fun provideScreenDao(appDatabase: AppDatabase) = appDatabase.cacheDao()

    single { provideAppDatabase(get()) }
    single { provideScreenDao(get()) }
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

    viewModel {
        CryptoInfoViewModel(
            homeInteractors = get()
        )
    }

    single<GeminiApiService> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(GeminiApiService::class.java)
    }
}