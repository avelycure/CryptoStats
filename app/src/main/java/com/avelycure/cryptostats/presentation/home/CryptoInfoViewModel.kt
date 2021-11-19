package com.avelycure.cryptostats.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.domain.interactors.*
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
    val homeInteractors: HomeInteractors
) : ViewModel() {
    var firstStart = true

    private val _state: MutableLiveData<CryptoInfoState> = MutableLiveData()
    val state: LiveData<CryptoInfoState>
        get() = _state

    private val compositeDisposable = CompositeDisposable()

    fun onTrigger(event: CryptoInfoEvent) {
        when (event) {
            is CryptoInfoEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
        }
    }

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
            //compositeDisposable.addAll()
            requestPriceFeed(pair)
            requestCandles(symbol, timeFrame)

            requestTickerV2(symbol)
            requestTickerV1(symbol)
            requestTradeHistory(symbol, limit)
        }
    }

    fun clear() {
        compositeDisposable.clear()
    }

    private fun requestCandles(symbol: String, timeFrame: String): Disposable {
        return homeInteractors.getCandles.execute(symbol, timeFrame)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseCandles(data) }, {
                Log.d("mytag", "error in candles: ${it.message}")
            }, {})
    }

    private fun requestPriceFeed(pair: String): Disposable {
        return homeInteractors.getCoinPrice.execute()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {
                Log.d("mytag", "Error in repo while fetching coin price: ${it.message}")
            }, {})
    }

    private fun requestTickerV1(symbol: String): Disposable {
        return homeInteractors.getTickerV1.execute(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV1(data) }, {
                //Log.d("mytag", "Error in repo  while fetching  ticker v1: ${it.message}")
            }, {})
    }

    private fun requestTickerV2(symbol: String): Disposable {
        return homeInteractors.getTickerV2.execute(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV2(data) }, {
                Log.d("mytag", "Error in repo  while fetching  ticker v2: ${it.message}")
            }, {})
    }

    private fun requestTradeHistory(symbol: String, limit: Int): Disposable {
        return homeInteractors.getTrades.execute(symbol, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTradeHistory(data) }, {
                Log.d("mytag", "Error in repo  while fetching  trades: ${it.message}")
            }, {})
    }

    private fun onResponseCandles(candles: DataState<List<Candle>>) {
        when (candles) {
            is DataState.DataRemote -> handleCandles(candles.data, true)
            is DataState.DataCache -> handleCandles(candles.data, false)
            is DataState.Response -> appendToMessageQueue(candles.uiComponent)
        }
    }

    private fun onResponsePriceFeed(data: DataState<List<CoinPrice>>, pair: String) {
        when (data) {
            is DataState.DataRemote -> handlePriceFeed(data.data, pair, true)
            is DataState.DataCache -> handlePriceFeed(data.data, pair, false)
            is DataState.Response -> appendToMessageQueue(data.uiComponent)
        }
    }

    private fun onResponseTickerV1(data: DataState<TickerV1>) {
        when (data) {
            is DataState.DataRemote -> handleTickerV1(data.data, true)
            is DataState.DataCache -> handleTickerV1(data.data, false)
            is DataState.Response -> appendToMessageQueue(data.uiComponent)
        }
    }

    private fun onResponseTickerV2(data: DataState<TickerV2>) {
        Log.d("mytag","got response tickerv2")
        when (data) {
            is DataState.DataRemote -> handleTickerV2(data.data, true)
            is DataState.DataCache -> handleTickerV2(data.data, false)
            is DataState.Response -> appendToMessageQueue(data.uiComponent)
        }
    }

    private fun onResponseTradeHistory(data: DataState<List<Trade>>) {
        when (data) {
            is DataState.DataRemote -> handleTradeHistory(data.data, true)
            is DataState.DataCache -> handleTradeHistory(data.data, false)
            is DataState.Response -> appendToMessageQueue(data.uiComponent)
        }
    }

    private fun handleCandles(data: List<Candle>, remoteData: Boolean) {
        homeInteractors.prepareCandles.execute(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ dataForChart ->
                val newStat = _state.value?.statistic?.copy(
                    candles = dataForChart
                ) ?: Statistic24h()

                _state.value = state.value?.copy(
                    statistic = newStat,
                    remoteData = remoteData
                )
            }, {}, {})
    }

    private fun handlePriceFeed(data: List<CoinPrice>, pair: String, remoteData: Boolean) {
        Log.d("mytag", "got data: $remoteData $data")
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
        /*val queue: Queue<UIComponent> = Queue(mutableListOf())
        for (i in 0 until _state.value!!.errorQueue.count() - 1)
            _state.value!!.errorQueue.poll()?.let { queue.add(it) }

        val last = _state.value!!.errorQueue.poll()
        if (last != null) {
            queue.add(last)

            if ((last as UIComponent.Dialog).description != (uiComponent as UIComponent.Dialog).description) {
                Log.d("mytag", "not equal errors")
                queue.add(uiComponent)
                _state.value = _state.value!!.copy(errorQueue = queue)
            }
        } else {
            queue.add(uiComponent)
            _state.value = _state.value!!.copy(errorQueue = queue)
        }
*/
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
            //Log.d("mytag", "Nothing to remove from MessageQueue")
        }
    }
}