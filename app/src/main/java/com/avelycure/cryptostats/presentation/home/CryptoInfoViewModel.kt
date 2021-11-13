package com.avelycure.cryptostats.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import io.reactivex.rxjava3.core.Observable
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.models.TickerV2
import com.avelycure.cryptostats.domain.state.DataState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CryptoInfoViewModel(
    private val repo: ICryptoRepo
) : ViewModel() {

    private val _state: MutableLiveData<CryptoInfoState> = MutableLiveData()
    val state: LiveData<CryptoInfoState>
        get() = _state

    private val compositeDisposable = CompositeDisposable()

    init {
        _state.value = CryptoInfoState(
            statistic = Statistic24h(),
            coinPrice = CoinPrice(),
            tickerV2 = TickerV2(),
            trades = emptyList()
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

    private fun requestCandles(symbol: String, timeFrame: String): Disposable {
        return repo.getCandles(symbol, timeFrame)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseCandles(data) }, {
                Log.d("mytag", "error: ${it.message}")
            }, {})
    }

    private fun requestTickerV2(symbol: String): Disposable {
        return repo.getTickerV2(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV2(data) }, {
                Log.d("mytag", "Error: ${it.message}")
            }, {})
    }

    private fun requestPriceFeed(pair: String): Disposable {
        return repo.getPriceFeed()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {
                Log.d("mytag", "Errorpf: ${it.message}")
            }, {})
    }

    private fun requestTickerV1(symbol: String): Disposable {
        return repo.getTickerV1(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV1(data) }, {}, {})
    }

    private fun requestTradeHistory(symbol: String, limit: Int): Disposable {
        return repo.getTrades(symbol, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTradeHistory(data) }, {}, {})
    }

    private fun onResponseTradeHistory(data: DataState<List<Trade>>) {
        if (data is DataState.DataRemote) {
            val trades: List<Trade> = data.data.map { tradeHistory ->
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
        if (data is DataState.DataCache) {
            val trades: List<Trade> = data.data.map { tradeHistory ->
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
    }

    private fun onResponseTickerV1(data: DataState<TickerV1>) {
        if (data is DataState.DataRemote) {
            val newTicker = _state.value?.tickerV2?.copy(
                bid = data.data.bid,
                ask = data.data.ask
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
                tickerV2 = newTicker
            )
        }
        if (data is DataState.DataCache) {
            val newTicker = _state.value?.tickerV2?.copy(
                bid = data.data.bid,
                ask = data.data.ask
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
                tickerV2 = newTicker
            )
        }
    }

    private fun onResponsePriceFeed(data: DataState<List<CoinPrice>>, pair: String) {
        if (data is DataState.DataRemote) {
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
        if (data is DataState.DataCache) {
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

    private fun onResponseTickerV2(data: DataState<TickerV2>) {
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

    private fun onResponseCandles(candles: DataState<List<Candle>>) {
        if (candles is DataState.DataRemote) {
            val dataForChart = candles.data

            val first = dataForChart[0].time

            val dataForChartCopy = arrayListOf<Candle>()
            for (i in 0 until dataForChart.size)
                if (i % 3 == 0)
                    dataForChartCopy.add(dataForChart[i].copy(time = (dataForChart[i].time - first) / 600F))

            val newStat = _state.value?.statistic?.copy(
                candles = dataForChartCopy
            ) ?: Statistic24h()

            _state.value = state.value?.copy(
                statistic = newStat
            )
        }
        if (candles is DataState.DataCache) {
            if (candles is DataState.DataRemote) {
                val dataForChart = candles.data

                val first = dataForChart[0].time

                val dataForChartCopy = arrayListOf<Candle>()
                for (i in 0 until dataForChart.size)
                    if (i % 3 == 0)
                        dataForChartCopy.add(dataForChart[i].copy(time = (dataForChart[i].time - first) / 600F))

                val newStat = _state.value?.statistic?.copy(
                    candles = dataForChartCopy
                ) ?: Statistic24h()

                _state.value = state.value?.copy(
                    statistic = newStat
                )
            }
        }
    }
}