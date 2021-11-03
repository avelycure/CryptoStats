package com.avelycure.cryptostats.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.AuctionHistoryResponse
import com.avelycure.cryptostats.data.CandlesResponse
import com.avelycure.cryptostats.data.GeminiApiService
import com.avelycure.cryptostats.data.TradeHistory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CryptoInfoFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private lateinit var btn: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)
        lineChart = view.findViewById(R.id.chart)
        btn = view.findViewById(R.id.btn)

        btn.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()

            val apiService = retrofit.create(GeminiApiService::class.java)

            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(
                apiService.getCandles("btcusd")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ data -> onResponse(data) },
                        { t -> onFailure(t) },
                        { onFinish() })
            )
        }
        return view
    }

    private fun onFinish() {

    }

    private fun onFailure(t: Throwable) {
        Toast.makeText(requireContext(), "error" + t.message, Toast.LENGTH_LONG).show()
    }

    private fun onResponse(data: List<List<Float>>) {

        val dataForChart3 = arrayListOf<Entry>()

        for (i in 0 until data.size)
            dataForChart3.add(Entry(data[i][0], (data[i][1] + data[i][4]) / 2F))

        dataForChart3.sortBy { it.x }

        val dataSet1 = LineDataSet(dataForChart3, "Data set 3")
        dataSet1.setDrawIcons(false)
        dataSet1.setDrawCircleHole(false)
        dataSet1.setDrawFilled(true)

        val dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(dataSet1)

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}
