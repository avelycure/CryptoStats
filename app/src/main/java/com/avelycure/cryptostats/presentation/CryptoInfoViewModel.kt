package com.avelycure.cryptostats.presentation

import android.content.Context
import android.net.NetworkRequest
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV1
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.models.TradeHistory
import com.avelycure.cryptostats.data.network.NetworkStatus
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CryptoInfoViewModel(
    private val repo: ICryptoRepo
) : ViewModel() {

    var context: Context? = null

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
        repo.getCandles(symbol, timeFrame)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseCandles(data) }, {
                Log.d("mytag", "error: ${it.message}")
            }, {})
    }

    fun requestTickerV2(symbol: String) {
        makeTickerRequest(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { data -> onResponseTickerV2(data) }
    }

    private fun makeTickerRequest(symbol: String): Single<TickerV2> {
        val networkStatus = NetworkStatus(context!!)
        return networkStatus.isOnlineSingle().flatMap { isOnline ->
            if (isOnline) {
                repo.getTickerV2(symbol)
                    .flatMap {
                        Single.fromCallable { it }
                    }
            } else {
                Single.fromCallable {
                    TickerV2(
                        "", 0f, 0f, 0f, 0f, emptyList<Float>(), 0f, 0f
                    )
                }
            }
        }
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

    fun requestTradeHistory(symbol: String, limit: Int) {
        repo.getTrades(symbol, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTradeHistory(data) }, {}, {})
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

    private fun onResponseTickerV2(data: TickerV2) {
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
            )
        )
    }

    private fun onResponseCandles(candles: List<List<Float>>) {
        /*val dataForChart = arrayListOf<Candle>()
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
        )*/
    }

    fun unixTimeToStringDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(timestamp)
        return sdf.format(netDate)
    }
}