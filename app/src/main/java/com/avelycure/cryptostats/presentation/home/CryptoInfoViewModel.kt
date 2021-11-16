package com.avelycure.cryptostats.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.interactors.*
import io.reactivex.rxjava3.core.Observable
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.models.TickerV2
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.domain.state.Queue
import com.avelycure.cryptostats.domain.state.UIComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CryptoInfoViewModel(
    private val getCandles: GetCandles,
    private val getTickerV2: GetTickerV2,
    private val getCoinPrice: GetCoinPrice,
    private val getTickerV1: GetTickerV1,
    private val getTrades: GetTrades
) : ViewModel() {
    var firstStart = true

    private val _state: MutableLiveData<CryptoInfoState> = MutableLiveData()
    val state: LiveData<CryptoInfoState>
        get() = _state

    private val compositeDisposable = CompositeDisposable()

    init {
        _state.value = CryptoInfoState(
            statistic = Statistic24h(),
            coinPrice = CoinPrice(),
            tickerV2 = TickerV2(),
            trades = emptyList(),
            remoteData = true,
            errorQueue = Queue(mutableListOf())
        )
    }

    data class RequestParameters(
        val symbol: String,
        val timeFrame: String,
        val limit: Int,
        val pair: String
    )

    fun requestData(requestParameters: RequestParameters) {
        with(requestParameters) {
            compositeDisposable.addAll(
                requestPriceFeed(pair),
                requestCandles(symbol, timeFrame),
                requestTickerV1(symbol),
                requestTickerV2(symbol),
                requestTradeHistory(symbol, limit),
            )
        }
    }

    fun clear(){
        compositeDisposable.clear()
    }

    private fun requestCandles(symbol: String, timeFrame: String): Disposable {
        return getCandles.execute(symbol, timeFrame)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseCandles(data) }, {
                Log.d("mytag", "error in candles: ${it.message}")
            }, {})
    }

    private fun requestPriceFeed(pair: String): Disposable {
        return getCoinPrice.execute()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {
                Log.d("mytag", "Error in repo while fetching coin price: ${it.message}")
            }, {})
    }

    private fun requestTickerV1(symbol: String): Disposable {
        return getTickerV1.execute(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV1(data) }, {
                Log.d("mytag", "Error in repo  while fetching  ticker v1: ${it.message}")
            }, {})
    }

    private fun requestTickerV2(symbol: String): Disposable {
        return getTickerV2.execute(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV2(data) }, {
                Log.d("mytag", "Error in repo  while fetching  ticker v2: ${it.message}")
            }, {})
    }

    private fun requestTradeHistory(symbol: String, limit: Int): Disposable {
        return getTrades.execute(symbol, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTradeHistory(data) }, {
                Log.d("mytag", "Error in repo  while fetching  trades: ${it.message}")
            }, {})
    }

    private fun onResponseCandles(candles: DataState<List<Candle>>) {
        if (candles is DataState.DataRemote)
            handleCandles(candles.data, true)
        if (candles is DataState.DataCache)
            handleCandles(candles.data, false)
    }

    private fun onResponsePriceFeed(data: DataState<List<CoinPrice>>, pair: String) {
        if (data is DataState.DataRemote) {
            Log.d("mytag", "View model remote")
            handlePriceFeed(data.data, pair, true)
        }
        if (data is DataState.DataCache) {
            Log.d("mytag", "View model cache")
            handlePriceFeed(data.data, pair, false)
        }
    }

    private fun onResponseTickerV1(data: DataState<TickerV1>) {
        if (data is DataState.DataRemote)
            handleTickerV1(data.data, true)
        if (data is DataState.DataCache)
            handleTickerV1(data.data, false)
    }

    private fun onResponseTickerV2(data: DataState<TickerV2>) {
        Log.d("mytag", "Got response from ticker")
        if (data is DataState.DataRemote) {
            Log.d("mytag", "Remote")
            handleTickerV2(data.data, true)
        }
        if (data is DataState.DataCache) {
            Log.d("mytag", "Cache")
            handleTickerV2(data.data, false)
        }
    }

    private fun onResponseTradeHistory(data: DataState<List<Trade>>) {
        if (data is DataState.DataRemote)
            handleTradeHistory(data.data, true)
        if (data is DataState.DataCache)
            handleTradeHistory(data.data, false)
    }

    private fun handleCandles(data: List<Candle>, remoteData: Boolean) {
        val dataForChart = data

        val first = dataForChart[0].time

        val dataForChartCopy = arrayListOf<Candle>()
        for (i in 0 until dataForChart.size)
            if (i % 3 == 0)
                dataForChartCopy.add(dataForChart[i].copy(time = (dataForChart[i].time - first) / 600F))

        val newStat = _state.value?.statistic?.copy(
            candles = dataForChartCopy
        ) ?: Statistic24h()

        _state.value = state.value?.copy(
            statistic = newStat,
            remoteData = remoteData
        )
    }

    private fun handlePriceFeed(data: List<CoinPrice>, pair: String, remoteData: Boolean) {
        for (i in data)
            if (i.pair == pair) {
                _state.value = _state.value?.copy(
                    coinPrice = CoinPrice(
                        price = i.price,
                        percentChange24h = i.percentChange24h
                    ),
                    remoteData = remoteData
                )
                break
            }
    }

    private fun handleTickerV1(data: TickerV1, remoteData: Boolean) {
        val newTicker = _state.value?.tickerV2?.copy(
            bid = data.bid,
            ask = data.ask
        ) ?: TickerV2(
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
            tickerV2 = newTicker,
            remoteData = true
        )
    }

    private fun handleTickerV2(data: TickerV2, remoteData: Boolean) {
        val dataForChart = arrayListOf<Point>()
        for (i in 0 until data.changes.size)
            dataForChart.add(Point(24F - i.toFloat(), data.changes[i]))

        dataForChart.sortBy { it.x }

        _state.value = state.value?.copy(
            statistic = Statistic24h(
                symbol = data.symbol,
                high = data.high,
                low = data.low,
                open = data.open,
                changes = dataForChart,
                candles = _state.value?.statistic?.candles?.toList() ?: emptyList()
            ),
            remoteData = remoteData
        )
    }

    private fun handleTradeHistory(data: List<Trade>, remoteData: Boolean) {
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
            trades = trades,
            remoteData = remoteData
        )
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        val queue: Queue<UIComponent> = Queue(mutableListOf())
        for (i in 0 until _state.value!!.errorQueue.count())
            _state.value!!.errorQueue.poll()?.let { queue.add(it) }
        queue.add(uiComponent)
        _state.value = _state.value!!.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = _state.value!!.errorQueue
            queue.remove()
            _state.value = _state.value!!.copy(errorQueue = queue)
        } catch (e: Exception) {
            Log.d("mytag", "Nothing to remove from MessageQueue")
        }
    }
}