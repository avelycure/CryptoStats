package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.mappers.toCoinPrice
import com.avelycure.cryptostats.data.remote.models.mappers.toCoinPrice
import com.avelycure.cryptostats.data.remote.models.mappers.toEntityPriceFeed
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.CoinPrice
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class GetCoinPrice(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) {
    fun execute(): Observable<DataState<List<CoinPrice>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline) {
                repo.getPriceFeedFromRemote().flatMap { priceFeed ->
                    Observable.fromCallable { DataState.DataRemote(data = priceFeed.map { it.toCoinPrice() }) }
                }
            } else
                Observable.fromCallable {
                    DataState.DataCache(
                        data = repo.getPriceFeedFromCache().map { it.toCoinPrice() })
                }
        }.retryWhen { error ->
            Log.d("mytag", "Error in repo")
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
            //maybe add throw exception or DataState.Error
        }
    }
}