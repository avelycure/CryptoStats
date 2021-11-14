package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toTickerV2
import com.avelycure.cryptostats.data.remote.models.mappers.toTickerV2
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.TickerV2
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
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
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }
}