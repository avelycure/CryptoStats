package com.avelycure.cryptostats.domain.interactors

import android.util.Log
import com.avelycure.cryptostats.data.local.entities.EntityPriceFeed
import com.avelycure.cryptostats.data.local.entities.mappers.toCoinPrice
import com.avelycure.cryptostats.data.remote.models.mappers.toCoinPrice
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.models.CoinPrice
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.UIComponent
import com.avelycure.cryptostats.utils.exceptions.EmptyCacheException
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import io.reactivex.rxjava3.core.Observable
import java.net.UnknownHostException
import java.util.NoSuchElementException
import java.util.concurrent.TimeUnit

class GetCoinPrice(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) {
    fun execute(): Observable<DataState<List<CoinPrice>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline)
                repo.getPriceFeedFromRemote().flatMap { priceFeed ->
                    Observable.fromCallable { DataState.DataRemote(data = priceFeed.map { it.toCoinPrice() }) }
                }
            else {
                Log.d("mytag", "cache price feed")
                Observable.fromCallable {
                    DataState.DataCache(
                        data = repo.getPriceFeedFromCache().map {
                            it.toCoinPrice()
                        }
                    )
                }
            }
        }.onErrorReturn { error ->
            Log.d("mytag", "GOT ERROR PRICE FEED")
            when (error) {
                is EmptyCacheException -> {
                    Log.d("mytag", "price feed empty list")
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            description = error.message ?: "No cache data, turn on the Internet"
                        )
                    )
                }
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