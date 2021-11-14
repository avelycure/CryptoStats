package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toTrade
import com.avelycure.cryptostats.data.remote.models.mappers.toTrade
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.Trade
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

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
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }
}