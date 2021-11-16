package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toTickerV1
import com.avelycure.cryptostats.data.remote.models.mappers.toTickerV1
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.TickerV1
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

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
        }
    }
}