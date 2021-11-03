package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.api_service.GeminiApiService
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CryptoRepo(
): ICryptoRepo {

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()

    val apiService = retrofit.create(GeminiApiService::class.java)

    /*override fun getCandles(symbol: String): Observable<List<List<Float>>> {

    }*/

    override fun hello() = "Hello"
}