package com.avelycure.cryptostats.di

import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.api_service.GeminiApiService
import com.avelycure.cryptostats.data.network.INetworkStatus
import com.avelycure.cryptostats.data.network.NetworkStatus
import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.presentation.CryptoInfoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<ICryptoRepo> { CryptoRepo(get(), get()) }

    single<INetworkStatus> { NetworkStatus(get()) }

    viewModel { CryptoInfoViewModel(get(), get()) }

    single<GeminiApiService> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(GeminiApiService::class.java)
    }
}