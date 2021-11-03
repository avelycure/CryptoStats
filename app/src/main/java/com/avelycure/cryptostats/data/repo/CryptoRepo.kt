package com.avelycure.cryptostats.data.repo

import com.avelycure.cryptostats.data.api_service.GeminiApiService
import io.reactivex.rxjava3.core.Observable

class CryptoRepo(
    private val apiService: GeminiApiService
): ICryptoRepo {

    override fun getCandles(symbol: String): Observable<List<List<Float>>> {
        return apiService.getCandles(symbol)
    }

}