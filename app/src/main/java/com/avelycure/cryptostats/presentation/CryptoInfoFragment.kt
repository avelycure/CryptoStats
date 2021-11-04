package com.avelycure.cryptostats.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avelycure.cryptostats.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class CryptoInfoFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private lateinit var btn: AppCompatButton
    private lateinit var tvCoinValue: AppCompatTextView
    private lateinit var tvCoinValueChanging: AppCompatTextView

    private val cryptoInfoViewModel: CryptoInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)
        lineChart = view.findViewById(R.id.chart)
        initViews(view)

        btn.setOnClickListener {
            cryptoInfoViewModel.requestTicker("btcusd")
        }

        cryptoInfoViewModel.chartData.observe(viewLifecycleOwner, { chartData ->
            plotGraphic(chartData)
        })

        return view
    }

    private fun initViews(view: View) {
        setChart()

        btn = view.findViewById(R.id.btn)
        tvCoinValue = view.findViewById(R.id.ci_tv_coin_value)
        tvCoinValueChanging = view.findViewById(R.id.ci_tv_coin_value_change_in_last_24h)
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
            axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

            xAxis.labelCount = 6

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${(24F - value).roundToInt()}h"
                }
            }

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.setCenterAxisLabels(true)
        }
    }

    fun getFormatTimeWithTZ(currentTime: Date): String {
        val timeZoneDate = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeZoneDate.format(currentTime)
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
