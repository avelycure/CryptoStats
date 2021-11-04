package com.avelycure.cryptostats.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.roundToInt

class CryptoInfoViewModel(
    private val repo: ICryptoRepo
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
            )
        )
    }

    fun requestCandles(symbol: String, timeFrame: String) {
        repo.getCandles(symbol, timeFrame)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponse(data) }, {
                Log.d("mytag", "error: ${it.message}")
            }, {})
    }

    fun requestTickerV2(symbol: String) {
        repo.getTickerV2(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV2(data) }, {}, {})
    }

    fun requestPriceFeed(pair: String) {
        repo.getPriceFeed()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {}, {})
    }

    fun requestTickerV1(symbol: String) {
        repo.getTickerV1(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTickerV1(data) }, {}, {})
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

    private fun onResponseTickerV2(data: TickerV2) {
        val dataForChart = arrayListOf<Point>()
        for (i in 0 until data.changes.size)
            dataForChart.add(Point(24F - i.toFloat(), data.changes[i]))

        dataForChart.sortBy { it.x }

        val newCandles = _state.value?.statistic?.candles?.toList() ?: emptyList()

        _state.value = state.value?.copy(
            statistic = Statistic24h(
                symbol = data.symbol,
                high = data.high,
                low = data.low,
                open = data.open,
                changes = dataForChart,
                candles = newCandles
            )
        )
    }

    private fun onResponse(candles: List<List<Float>>) {
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
}