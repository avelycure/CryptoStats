package com.avelycure.cryptostats.domain.interactors

import com.avelycure.cryptostats.data.local.entities.mappers.toTickerV1
import com.avelycure.cryptostats.data.remote.models.mappers.toTickerV1
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.TickerV1
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.UIComponent
import com.avelycure.cryptostats.utils.exceptions.EmptyCacheException
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.net.UnknownHostException
import java.util.NoSuchElementException

//todo when switching from remote to cache we got error, may be it is needed to do smth
class GetTickerV1(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) {
    fun execute(symbol: String): Observable<DataState<TickerV1>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                repo.getTickerV1FromRemote(symbol).flatMap { tickerV1 ->
                    Observable.fromCallable { DataState.DataRemote(data = tickerV1.toTickerV1()) }
                }
            } else
                Observable.fromCallable {
                    DataState.DataCache(
                        data = repo.getTickerV1FromCache().toTickerV1()
                    )
                }
        }.onErrorReturn { error ->
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