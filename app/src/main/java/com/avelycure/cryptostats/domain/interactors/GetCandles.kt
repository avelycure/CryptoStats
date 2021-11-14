package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toCandleList
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.Candle
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class GetCandles(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) {
    fun execute(symbol: String, timeFrame: String): Observable<DataState<List<Candle>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                repo.getCandlesFromRemote(symbol, timeFrame).flatMap { candles ->
                    Observable.fromCallable { DataState.DataRemote(data = candles.toCandleList()) }
                }
            } else
                Observable.fromCallable {
                    DataState.DataCache(
                        data = repo.getCandlesFromCache().toCandleList()
                    )
                }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo candles")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }
}