package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toCandleList
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.Candle
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.UIComponent
import com.avelycure.cryptostats.utils.exceptions.EmptyCacheException
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.net.UnknownHostException
import java.util.NoSuchElementException

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
        }.onErrorReturn { error ->
            Log.d("mytag", "GOT ERROR CANDLES")
            when (error) {
                is EmptyCacheException -> DataState.Response(
                    uiComponent = UIComponent.Dialog(
                        description = error.message ?: "No cache data, turn on the Internet"
                    )
                )
                is UnknownHostException -> DataState.Response(
                    uiComponent = UIComponent.Dialog(
                        description = "No internet connection"
                    )
                )
                else -> DataState.Response(
                    uiComponent = UIComponent.Dialog(
                        description = error.message ?: "Unknown error occurred"
                    )
                )
            }
        }
    }
}