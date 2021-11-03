package com.avelycure.cryptostats.data.repo

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Path

interface ICryptoRepo {
    //fun getCandles(symbol: String): Observable<List<List<Float>>>
    fun hello(): String
}