package com.avelycure.cryptostats.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Point
import com.avelycure.cryptostats.domain.Statistic24h
import com.github.mikephil.charting.data.Entry
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

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
                emptyList()
            ),
            coinPrice = CoinPrice(
                price = "",
                percentChange24h = ""
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

    fun requestTicker(symbol: String) {
        repo.getTicker(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTicker(data) }, {}, {})
    }

    fun requestPriceFeed(pair: String) {
        repo.getPriceFeed()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponsePriceFeed(data, pair) }, {}, {})
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

    private fun onResponseTicker(data: TickerV2) {
        val dataForChart = arrayListOf<Point>()
        for (i in 0 until data.changes.size)
            dataForChart.add(Point(24F - i.toFloat(), data.changes[i]))

        dataForChart.sortBy { it.x }

        _state.value = state.value?.copy(
            statistic = Statistic24h(
                symbol = data.symbol,
                high = data.high,
                low = data.low,
                changes = dataForChart
            )
        )
    }

    private fun onResponse(data: List<List<Float>>) {
        val dataForChart = arrayListOf<Point>()
        for (i in 0 until data.size)
            dataForChart.add(Point(data[i][0], data[i][1]))
        dataForChart.sortBy { it.x }

        val newStat = _state.value?.statistic?.copy(
            changes = dataForChart
        ) ?: Statistic24h("", 0F, 0F, emptyList())

        _state.value = state.value?.copy(
            statistic = newStat
        )

        Log.d("mytag", "" + _state.value?.statistic?.changes?.toList().toString())
    }
}