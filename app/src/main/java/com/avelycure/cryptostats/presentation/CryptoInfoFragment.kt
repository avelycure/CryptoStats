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
    private lateinit var observable: Observable<AuctionHistoryResponse>
    private lateinit var btn: AppCompatButton
    private lateinit var tv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)
        lineChart = view.findViewById(R.id.chart)
        btn = view.findViewById(R.id.btn)
        tv = view.findViewById(R.id.tv_response)

        btn.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()

            val apiService = retrofit.create(GeminiApiService::class.java)
            Log.d("mytag", "fragment")


            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(
                apiService.getCandles()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ data -> onResponse3(data) },
                        { t -> onFailure3(t) },
                        { onFinish3() })
            )
        }

        return view
    }

    private fun onFinish() {
        val dataSet1 = LineDataSet(dataForChart, "Data set 1")
        dataSet1.setDrawIcons(false)
        dataSet1.setDrawCircleHole(false)
        dataSet1.setDrawFilled(true)

        val dataSets: ArrayList<ILineDataSet> = arrayListOf(
            dataSet1
        )

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun onFailure(t: Throwable) {
        Toast.makeText(requireContext(), "error" + t.message, Toast.LENGTH_LONG).show()
    }

    val dataForChart: ArrayList<Entry> = arrayListOf()

    private fun onResponse(data: List<AuctionHistoryResponse>) {
        Toast.makeText(requireContext(), "Got response" + data.size, Toast.LENGTH_LONG).show()
        val auctionId = data[0].auction_id

        for (i in data)
            if (i.auction_id == auctionId)
                dataForChart.add(Entry(i.timestamp.toFloat(), i.auction_price))
    }

    /*
    * -----------------------------
    * */

    private fun onFinish2() {
        /*if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
            val set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = dataForChart2
            set1.notifyDataSetChanged()
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {*/
        val dataSet1 = LineDataSet(dataForChart2, "Data set 1")
        dataSet1.setDrawIcons(false)
        //dataSet1.setDrawCircleHole(false)
        dataSet1.setDrawFilled(true)

        val dataSets: ArrayList<ILineDataSet> = arrayListOf(
            dataSet1
        )

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
        //}
    }

    private fun onFailure2(t: Throwable) {
        Toast.makeText(requireContext(), "error" + t.message, Toast.LENGTH_LONG).show()
    }

    val dataForChart2: ArrayList<Entry> = arrayListOf()

    private fun onResponse2(data: List<TradeHistory>) {
        Toast.makeText(requireContext(), "Got response" + data.size, Toast.LENGTH_LONG).show()
        dataForChart2.clear()

        lineChart.setMaxVisibleValueCount(50)

        for (i in data)
            if (i.type == "sell")
                dataForChart2.add(Entry(i.timestamp.toFloat() / 1000F / 60F, i.price.toFloat()))
        //Toast.makeText(requireContext(), "Got response" + dataForChart2.size, Toast.LENGTH_LONG)
        //    .show()
    }

    fun dateConverter(ms: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = ms
        return SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS").format(cal.time)
    }


    private fun onFinish3() {

    }

    private fun onFailure3(t: Throwable) {
        Toast.makeText(requireContext(), "error" + t.message, Toast.LENGTH_LONG).show()
    }


    private fun onResponse3(data: List<List<Float>>) {

        val dataForChart3 = arrayListOf<Entry>()

        for (i in 0 until data.size) {
            dataForChart3.add(Entry(data[i][0], (data[i][1] + data[i][4]) / 2F))
        }

        dataForChart3.sortBy { it.x }

        val dataSet1 = LineDataSet(dataForChart3, "Data set 3")
        Toast.makeText(requireContext(), "Got response" + dataSet1.entryCount, Toast.LENGTH_SHORT)
            .show()
        dataSet1.setDrawIcons(false)
        dataSet1.setDrawCircleHole(false)
        dataSet1.setDrawFilled(true)

        val dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(dataSet1)

        //for (i in 1 until dataSets.size)
        //    tv.text = "${tv.text}, ${dataSets[0].entryCount}"

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}
