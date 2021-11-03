package com.avelycure.cryptostats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.github.mikephil.charting.data.Entry
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CryptoInfoViewModel(
    val repo: ICryptoRepo
): ViewModel() {

    private val candles: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
    fun getCandles(): LiveData<ArrayList<Entry>>{
        return candles
    }

    fun requestCandles(symbol: String){
        repo.getCandles("btcusd")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ data -> onResponse(data) }, {}, {})
    }

    private fun onResponse(data: List<List<Float>>) {
        val dataForChart = arrayListOf<Entry>()
        for (i in 0 until data.size)
            dataForChart.add(Entry(data[i][0], data[i][1]))
        dataForChart.sortBy { it.x }

        candles.postValue(dataForChart)
    }
}