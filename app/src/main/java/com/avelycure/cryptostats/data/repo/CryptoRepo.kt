package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.api_service.GeminiApiService
import io.reactivex.rxjava3.core.Observable
import org.koin.core.component.KoinComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CryptoRepo(
    val apiService: GeminiApiService
): ICryptoRepo, KoinComponent {

    override fun getCandles(symbol: String): Observable<List<List<Float>>> {
        return apiService.getCandles(symbol)
    }

    override fun hello() = "Hello"
}