package com.avelycure.cryptostats.domain.interactors

import com.avelycure.cryptostats.data.local.entities.mappers.toTrade
import com.avelycure.cryptostats.data.remote.models.mappers.toTrade
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.Trade
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable

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
        }
    }
}