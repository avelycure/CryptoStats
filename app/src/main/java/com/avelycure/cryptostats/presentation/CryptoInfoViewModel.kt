package com.avelycure.cryptostats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.models.PriceFeed
import com.avelycure.cryptostats.data.models.TickerV2
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.github.mikephil.charting.data.Entry
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CryptoInfoViewModel(
    private val repo: ICryptoRepo
) : ViewModel() {

    private val _chartData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
    val chartData: LiveData<ArrayList<Entry>>
        get() = _chartData

    private val _coinPrice: MutableLiveData<PriceFeed> = MutableLiveData()
    val coinPrice: LiveData<PriceFeed>
        get() = _coinPrice

    private val _stats24: MutableLiveData<TickerV2> = MutableLiveData()
    val stats24: LiveData<TickerV2>
        get() = _stats24

    fun requestCandles(symbol: String) {
        repo.getCandles(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponse(data) }, {}, {})
    }

    fun requestTicker(symbol: String) {
        repo.getTicker(symbol)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponseTicker(data) }, {}, {})
    }

    fun requestPriceFeed(pair: String){
        repo.getPriceFeed()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({data -> onResponsePriceFeed(data, pair)},{},{})
    }

    private fun onResponsePriceFeed(data: List<PriceFeed>, pair: String) {
        for(i in data)
            if(i.pair==pair){
                _coinPrice.postValue(PriceFeed(i.pair, i.price, i.percentChange24h))
                break
            }
    }

    private fun onResponseTicker(data: TickerV2) {
        val dataForChart = arrayListOf<Entry>()
        for (i in 0 until data.changes.size)
            dataForChart.add(Entry(24F - i.toFloat(), data.changes[i]))

        dataForChart.sortBy { it.x }
        _chartData.postValue(dataForChart)

        _stats24.postValue(data)
    }

    private fun onResponse(data: List<List<Float>>) {
        val dataForChart = arrayListOf<Entry>()
        for (i in 0 until data.size)
            dataForChart.add(Entry(data[i][0], data[i][1]))
        dataForChart.sortBy { it.x }

        _chartData.postValue(dataForChart)
    }
}