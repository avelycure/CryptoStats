package com.avelycure.cryptostats.presentation

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.models.*
import com.avelycure.cryptostats.data.network.INetworkStatus
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.data.room.dao.ScreenDao
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
                bid = 0f,
                ask = 0f,
                high = 0f,
                low = 0f,
                changes = emptyList(),
                close = 0f,
                symbol = "",
                open = 0f
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

    fun requestTicker(symbol: String) {
        makeTickerRequest(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTicker(data) }, {
                Log.d("mytag", "Error: ${it.message}")
            }, {})
    }

    private fun makeTickerRequest(symbol: String): Observable<DataState<Ticker>> {
        Log.d("mytag", "Make request to ticker")
        return repo.getTicker(symbol)
    }

    fun requestPriceFeed(pair: String) {
        makePriceFeedRequest()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {
                Log.d("mytag", "Errorpf: ${it.message}")
            }, {})
    }

    private fun makePriceFeedRequest(): Observable<DataState<List<PriceFeed>>> {
        return repo.getPriceFeed()
    }

    fun requestTickerV1(symbol: String) {
        makeTickerV1Request(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV1(data) }, {}, {})
    }

    private fun makeTickerV1Request(symbol: String): Observable<DataState<TickerV1Model>> {
        return repo.getTickerV1(symbol)
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

    private fun onResponseTickerV1(data: DataState<TickerV1Model>) {
        if(data is DataState.DataRemote){
            val newTicker = _state.value?.ticker?.copy(
                bid = data.data.bid,
                ask = data.data.ask
            ) ?: Ticker(
                bid = 0f,
                ask = 0f,
                high = 0f,
                low = 0f,
                changes = emptyList(),
                close = 0f,
                symbol = "",
                open = 0f
            )
            _state.value = _state.value?.copy(
                ticker = newTicker
            )
        }
        if(data is DataState.DataCache){
            val newTicker = _state.value?.ticker?.copy(
                bid = data.data.bid,
                ask = data.data.ask
            ) ?: Ticker(
                bid = 0f,
                ask = 0f,
                high = 0f,
                low = 0f,
                changes = emptyList(),
                close = 0f,
                symbol = "",
                open = 0f
            )
            _state.value = _state.value?.copy(
                ticker = newTicker
            )
        }
    }

    private fun onResponsePriceFeed(data: DataState<List<PriceFeed>>, pair: String) {
        if(data is DataState.DataRemote){
            Log.d("mytag", "View model remote")
            for (i in data.data)
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
        if(data is DataState.DataCache){
            Log.d("mytag", "View model cache")
            for (i in data.data)
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
    }

    private fun onResponseTicker(data: DataState<Ticker>) {
        Log.d("mytag", "Got response from ticker")
        if (data is DataState.DataRemote) {
        Log.d("mytag", "Remote")
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
        if (data is DataState.DataCache) {
        Log.d("mytag", "Cache")
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