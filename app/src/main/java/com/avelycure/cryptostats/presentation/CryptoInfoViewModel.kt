package com.avelycure.cryptostats.presentation

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.models.*
import com.avelycure.cryptostats.data.network.INetworkStatus
import com.avelycure.cryptostats.data.network.NetworkStatus
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import io.reactivex.rxjava3.core.Observable
import com.avelycure.cryptostats.domain.*
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class CryptoInfoViewModel(
    private val repo: ICryptoRepo,
    private val networkStatus: INetworkStatus
) : ViewModel() {

    private val _state: MutableLiveData<CryptoInfoState> = MutableLiveData()
    val state: LiveData<CryptoInfoState>
        get() = _state

    init {
        _state.value = CryptoInfoState(
            statistic = Statistic24h(
                symbol = "",
                high = 0F,
                low = 0F,
                open = 0F,
                emptyList(),
                emptyList()
            ),
            coinPrice = CoinPrice(
                price = "",
                percentChange24h = ""
            ),
            ticker = Ticker(
                bid = 0F,
                ask = 0F
            ),
            trades = emptyList()
        )
    }

    fun requestCandles(symbol: String, timeFrame: String) {
        makeRequestCandles(symbol, timeFrame)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseCandles(data) }, {
                Log.d("mytag", "error: ${it.message}")
            }, {})
    }

    private fun makeRequestCandles(
        symbol: String,
        timeFrame: String
    ): Observable<List<List<Float>>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline)
                repo.getCandles(symbol, timeFrame)
                    .repeatWhen { completed -> completed.delay(5, TimeUnit.SECONDS) }
            else
                Observable.fromCallable { emptyList<List<Float>>() }
        }.retryWhen { error ->
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
        }
    }

    fun requestTickerV2(symbol: String) {
        makeTickerRequest(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV2(data) }, {
                Log.d("mytag", "Error: ${it.message}")
            }, {})
    }

    /*private fun makeTickerRequest(symbol: String): Observable<TickerV2> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline)
                repo.getTickerV2(symbol)
                    .repeatWhen { completed ->
                        completed.delay(5, TimeUnit.SECONDS)
                    }
            else
                Observable.fromCallable {
                    TickerV2(
                        "", 0f, 0f, 0f, 0f, emptyList<Float>(), 0f, 0f
                    )
                }
        }.retryWhen { error ->
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
        }
    }*/


    private fun makeTickerRequest(symbol: String): Observable<DataState<TickerV2>> {
        return repo.getTickerV2(symbol)
    }

    fun requestPriceFeed(pair: String) {
        makePriceFeedRequest()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {
                Log.d("mytag", "Errorpf: ${it.message}")
            }, {})
    }

    private fun makePriceFeedRequest(): Observable<List<PriceFeed>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline)
                repo.getPriceFeed()
                    .repeatWhen { completed -> completed.delay(5, TimeUnit.SECONDS) }
            else
                Observable.fromCallable { emptyList<PriceFeed>() }
        }.retryWhen { error ->
            error.take(3).delay(100, TimeUnit.MILLISECONDS)
        }
    }

    fun requestTickerV1(symbol: String) {
        makeTickerV1Request(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV1(data) }, {}, {})
    }

    private fun makeTickerV1Request(symbol: String): Observable<TickerV1> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline)
                repo.getTickerV1(symbol)
                    .repeatWhen { completed -> completed.delay(5, TimeUnit.SECONDS) }
            else
                Observable.fromCallable { TickerV1(0f, 0f, 0f, VolumeBtcUsd(0f, 0f, 0)) }
        }.repeatWhen { error -> error.take(3).delay(100, TimeUnit.MILLISECONDS) }
    }

    fun requestTradeHistory(symbol: String, limit: Int) {
        makeRequestTradeHistory(symbol, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTradeHistory(data) }, {}, {})
    }

    private fun makeRequestTradeHistory(
        symbol: String,
        limit: Int
    ): Observable<List<TradeHistory>> {
        return networkStatus.isOnline().flatMap { isOnline ->
            if (isOnline)
                repo.getTrades(symbol, limit).repeatWhen { completed ->
                    completed.delay(5, TimeUnit.SECONDS)
                }
            else
                Observable.fromCallable { emptyList<TradeHistory>() }
        }.repeatWhen { error -> error.take(3).delay(100, TimeUnit.MILLISECONDS) }
    }

    private fun onResponseTradeHistory(data: List<TradeHistory>) {
        val trades: List<Trade> = data.map { tradeHistory ->
            Trade(
                timestampms = tradeHistory.timestampms,
                tid = tradeHistory.tid,
                price = tradeHistory.price,
                amount = tradeHistory.amount,
                type = tradeHistory.type
            )
        }

        _state.value = _state.value?.copy(
            trades = trades
        )
    }

    private fun onResponseTickerV1(data: TickerV1) {
        _state.value = _state.value?.copy(
            ticker = Ticker(
                bid = data.bid,
                ask = data.ask
            )
        )
    }

    private fun onResponsePriceFeed(data: List<PriceFeed>, pair: String) {
        for (i in data)
            if (i.pair == pair) {
                _state.value = _state.value?.copy(
                    coinPrice = CoinPrice(
                        price = i.price,
                        percentChange24h = i.percentChange24h
                    )
                )
                break
            }
    }

    private fun onResponseTickerV2(data: DataState<TickerV2>) {
        if(data is DataState.DataRemote){
            val dataForChart = arrayListOf<Point>()
            for (i in 0 until data.data.changes.size)
                dataForChart.add(Point(24F - i.toFloat(), data.data.changes[i]))

            dataForChart.sortBy { it.x }

            _state.value = state.value?.copy(
                statistic = Statistic24h(
                    symbol = data.data.symbol,
                    high = data.data.high,
                    low = data.data.low,
                    open = data.data.open,
                    changes = dataForChart,
                    candles = _state.value?.statistic?.candles?.toList() ?: emptyList()
                )
            )
        }
        if(data is DataState.DataCache){
            Log.d("mytag", "I got cache data")
        }
    }

    private fun onResponseCandles(candles: List<List<Float>>) {
        val dataForChart = arrayListOf<Candle>()
        for (candle in candles)
            dataForChart.add(
                Candle(
                    time = (candle[0] / 1000).roundToInt().toFloat(),
                    open = candle[1],
                    high = candle[2],
                    low = candle[3],
                    close = candle[4],
                )
            )

        dataForChart
            .sortBy { it.time }

        val first = dataForChart[0].time

        val dataForChartCopy = arrayListOf<Candle>()
        for (i in 0 until dataForChart.size)
            if (i % 3 == 0)
                dataForChartCopy.add(dataForChart[i].copy(time = (dataForChart[i].time - first) / 600F))

        dataForChart
            .sortBy { it.time }

        val newStat = _state.value?.statistic?.copy(
            candles = dataForChartCopy
        ) ?: Statistic24h("", 0F, 0F, 0F, emptyList(), emptyList())

        _state.value = state.value?.copy(
            statistic = newStat
        )
    }

    fun unixTimeToStringDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(timestamp)
        return sdf.format(netDate)
    }
}