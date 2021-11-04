package com.avelycure.cryptostats.presentation

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Statistic24h
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class CryptoInfoFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private lateinit var candleChart: CandleStickChart

    private lateinit var btn: AppCompatButton
    private lateinit var tvCoinValue: AppCompatTextView
    private lateinit var tvPercentageChanging24h: AppCompatTextView
    private lateinit var tvLowest24h: AppCompatTextView
    private lateinit var tvHighest24h: AppCompatTextView
    private lateinit var tvOpenPrice: AppCompatTextView
    private lateinit var tvPriceChange: AppCompatTextView
    private lateinit var currentTvBidPrice: AppCompatTextView
    private lateinit var currentTvAskPrice: AppCompatTextView

    private val cryptoInfoViewModel: CryptoInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)

        initViews(view)

        btn.setOnClickListener {
            cryptoInfoViewModel.requestTickerV2("btcusd")
            cryptoInfoViewModel.requestCandles("btcusd", "1m")
            cryptoInfoViewModel.requestPriceFeed("BTCUSD")
            cryptoInfoViewModel.requestTickerV1("btcusd")
        }

        cryptoInfoViewModel.state.observe(viewLifecycleOwner, { state ->
            plotLineGraphic(
                ArrayList(
                    state
                        .statistic
                        .changes
                        .map { point ->
                            Entry(point.x, point.y)
                        })
            )

            plotCandleGraphic(
                ArrayList(
                    state
                        .statistic
                        .candles
                        .map {
                            CandleEntry(
                                it.time,
                                it.high,
                                it.low,
                                it.open,
                                it.close
                            )
                        }
                )
            )

            updateStats(state.statistic)

            if (state.coinPrice.percentChange24h.isNotEmpty()) {
                tvPriceChange.text =
                    (state.coinPrice.price.toFloat() - state.statistic.high).toString()
                if (state.coinPrice.price.toFloat() - state.statistic.high > 0F)
                    tvPriceChange.setTextColor(Color.GREEN)
                else
                    tvPriceChange.setTextColor(Color.RED)

                currentTvAskPrice.text = state.ticker.ask.toString()
                currentTvBidPrice.text = state.ticker.bid.toString()
            }

            updatePrice(state.coinPrice)

        })

        return view
    }

    private fun updateStats(stats: Statistic24h) {
        tvLowest24h.text = stats.low.toString()
        tvHighest24h.text = stats.high.toString()
        tvOpenPrice.text = stats.open.toString()
    }

    private fun updatePrice(coinPrice: CoinPrice) {
        if (coinPrice.percentChange24h.isNotEmpty()) {
            tvCoinValue.text = coinPrice.price
            tvPercentageChanging24h.text = "${coinPrice.percentChange24h.toFloat() * 100F}%"

            if (coinPrice.percentChange24h.toFloat() > 0F)
                tvPercentageChanging24h.setTextColor(Color.GREEN)
            else
                tvPercentageChanging24h.setTextColor(Color.RED)
        }
    }

    private fun initViews(view: View) {
        lineChart = view.findViewById(R.id.chart)
        setLineChart()

        candleChart = view.findViewById(R.id.candle_stick_chart)
        setCandleChart()

        btn = view.findViewById(R.id.btn)
        tvCoinValue = view.findViewById(R.id.ci_tv_coin_value)
        tvPercentageChanging24h = view.findViewById(R.id.ci_tv_percent_change_in_last_24h)
        tvLowest24h = view.findViewById(R.id.ci_tv_lowest_in_last_24h)
        tvHighest24h = view.findViewById(R.id.ci_tv_highest_in_last_24h)
        tvOpenPrice = view.findViewById(R.id.ci_tv_open_price)
        tvPriceChange = view.findViewById(R.id.ci_tv_price_change)
        currentTvBidPrice = view.findViewById(R.id.ci_tv_current_price_bid)
        currentTvAskPrice = view.findViewById(R.id.ci_tv_current_ask)
    }

    private fun setCandleChart() {
        candleChart.apply {
            setDrawBorders(true)
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
            }
            axisLeft.apply {
                setDrawGridLines(false)
                textColor = Color.WHITE
            }
            requestDisallowInterceptTouchEvent(true)


            xAxis.setDrawGridLines(false)

            xAxis.setDrawLabels(false)
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.setAvoidFirstLastClipping(true)

            legend.isEnabled = false
        }
    }

    private fun plotCandleGraphic(data: ArrayList<CandleEntry>) {
        if (data.isNotEmpty()) {
            candleChart.apply {
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                xAxis.labelCount = 6

                setBackgroundColor(Color.DKGRAY)
                xAxis.setDrawGridLines(false)
                setDrawGridBackground(false)

                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawLabels(true)
                xAxis.setCenterAxisLabels(true)
            }


            val candleDataSet = CandleDataSet(data, "Set 1").apply {
                color = Color.rgb(80, 80, 80)
                shadowColor = Color.LTGRAY
                shadowWidth = 2F
                decreasingColor = Color.RED
                decreasingPaintStyle = Paint.Style.FILL
                increasingPaintStyle = Paint.Style.FILL
                increasingColor = Color.GREEN
                neutralColor = Color.BLUE
                setDrawValues(false)
            }

            candleChart.data = CandleData(candleDataSet)
            candleChart.invalidate()
        }
    }

    private fun setLineChart() {
        lineChart.apply {
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false

            setBackgroundColor(Color.DKGRAY)
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
                    return "-${(24F - value).roundToInt()}h"
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

    private fun plotLineGraphic(data1: ArrayList<Entry>) {
        val chartDataSets = arrayListOf<ILineDataSet>()

        val set1 = makeLineSet(data1, "Set 1")
        chartDataSets.add(set1)

        lineChart.data = LineData(chartDataSets)
        lineChart.invalidate()
    }

    private fun makeLineSet(data: ArrayList<Entry>, label: String): LineDataSet {
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
