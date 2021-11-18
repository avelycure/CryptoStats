package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toTickerV2
import com.avelycure.cryptostats.data.remote.models.mappers.toTickerV2
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.TickerV2
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.UIComponent
import com.avelycure.cryptostats.utils.exceptions.EmptyCacheException
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.net.UnknownHostException
import java.util.NoSuchElementException
import java.util.concurrent.TimeUnit

class GetTickerV2(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) {
    fun execute(symbol: String): Observable<DataState<TickerV2>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                repo.getTickerV2FromRemote(symbol).flatMap { tickerV2 ->
                    Observable.fromCallable { DataState.DataRemote(data = tickerV2.toTickerV2()) }
                }
            } else
                Observable.fromCallable {
                    DataState.DataCache(
                        data = repo.getTickerV2FromCache().toTickerV2()
                    )
                }
        }.onErrorReturn { error ->
            Log.d("mytag", "GOT ERROR TICKERV2")
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