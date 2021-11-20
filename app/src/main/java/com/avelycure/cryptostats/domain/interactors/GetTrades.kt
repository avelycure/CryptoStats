package com.avelycure.cryptostats.domain.interactors

import com.avelycure.cryptostats.data.local.entities.mappers.toTrade
import com.avelycure.cryptostats.data.remote.models.mappers.toTrade
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.Trade
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.UIComponent
import com.avelycure.cryptostats.utils.exceptions.EmptyCacheException
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.net.UnknownHostException
import java.util.NoSuchElementException

class GetTrades(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) {
    fun execute(symbol: String, limit: Int): Observable<DataState<List<Trade>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                repo.getTradesFromRemote(symbol, limit).flatMap { responseTradeHistory ->
                    Observable.fromCallable { DataState.DataRemote(data = responseTradeHistory.map { it.toTrade() }) }
                }
            } else
                Observable.fromCallable {
                    DataState.DataCache(
                        data = repo.getTradesFromCache().map { it.toTrade() })
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