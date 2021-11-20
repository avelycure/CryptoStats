package com.avelycure.cryptostats.di

import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.remote.api_service.GeminiApiService
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import com.avelycure.cryptostats.utils.network_utils.NetworkStatus
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val remoteModule = module {
    single<GeminiApiService> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(GeminiApiService::class.java)
    }

    single<INetworkStatus> { NetworkStatus(get()) }
}