package com.avelycure.cryptostats.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.GeminiApiService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class CryptoInfoFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private lateinit var btn: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)
        lineChart = view.findViewById(R.id.chart)
        setChart()
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

    private fun setChart() {
        lineChart.apply {
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false

            setPinchZoom(false)
            setDrawGridBackground(false)
            isDragEnabled = false
            isScaleXEnabled = false
            isScaleYEnabled = false

            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM

            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
            axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

            xAxis.labelCount = 4
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    /*val mm = (value / 1000F / 60F).roundToInt() % 60
                    val hh = (value / 1000F / (60F * 60F)).roundToInt() % 24*/
                    val date = Date(value.toLong())
                    //val mobileDateTime =
                    return getFormatTimeWithTZ(date)//formatter.format(Date(value.toString()))//"$hh:$mm"
                }
            }

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.setCenterAxisLabels(true)
        }
    }

    fun getFormatTimeWithTZ(currentTime:Date):String {
        val timeZoneDate = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeZoneDate.format(currentTime)
    }

    private fun onResponse(data: List<List<Float>>) {
        val dataForChart = arrayListOf<Entry>()
        for (i in 0 until data.size)
            dataForChart.add(Entry(data[i][0], data[i][1]))
        dataForChart.sortBy { it.x }

        plotGraphic(dataForChart)
    }

    private fun plotGraphic(data1: ArrayList<Entry>) {
        val chartDataSets = arrayListOf<ILineDataSet>()

        val set1 = makeSet(data1, "Set 1")
        chartDataSets.add(set1)

        lineChart.data = LineData(chartDataSets)
        lineChart.invalidate()
    }

    private fun makeSet(data: ArrayList<Entry>, label: String): LineDataSet {
        val set = LineDataSet(data, label)

        set.apply {
            setDrawIcons(false)
            setDrawCircleHole(false)
            setDrawCircles(false)
            setDrawFilled(true)

            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)
            color = Color.RED
            lineWidth = 2F
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2F
        }

        return set
    }


}
