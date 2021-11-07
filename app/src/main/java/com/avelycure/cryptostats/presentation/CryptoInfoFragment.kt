package com.avelycure.cryptostats.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private lateinit var swipeRefresh: SwipeRefreshLayout

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

        swipeRefresh.setOnRefreshListener {
            cryptoInfoViewModel.requestTickerV2("btcusd")
            cryptoInfoViewModel.requestCandles("btcusd", "1m")
            cryptoInfoViewModel.requestPriceFeed("BTCUSD")
            cryptoInfoViewModel.requestTickerV1("btcusd")
            swipeRefresh.isRefreshing = false
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
                ),
                "Set 1"
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

        tvCoinValue = view.findViewById(R.id.ci_tv_coin_value)
        tvPercentageChanging24h = view.findViewById(R.id.ci_tv_percent_change_in_last_24h)
        tvLowest24h = view.findViewById(R.id.ci_tv_lowest_in_last_24h)
        tvHighest24h = view.findViewById(R.id.ci_tv_highest_in_last_24h)
        tvOpenPrice = view.findViewById(R.id.ci_tv_open_price)
        tvPriceChange = view.findViewById(R.id.ci_tv_price_change)
        currentTvBidPrice = view.findViewById(R.id.ci_tv_current_price_bid)
        currentTvAskPrice = view.findViewById(R.id.ci_tv_current_ask)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_layout)
    }

    private fun setLineChart() {
        lineChart.apply {
            axisLeft.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            isDragEnabled = false
            isScaleYEnabled = false
            isScaleXEnabled = false
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM

            setDrawGridBackground(false)
            setPinchZoom(false)

            activity?.getColorFromAttr(R.attr.colorSurface)?.let { setBackgroundColor(it) }
            activity?.getColorFromAttr(R.attr.colorOnSurface)?.let { setNoDataTextColor(it) }

            axisRight.apply {
                textColor = activity?.getColorFromAttr(R.attr.colorOnSurface) ?: Color.BLACK

                setDrawGridLines(false)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            }

            xAxis.apply {
                labelCount = 6
                position = XAxis.XAxisPosition.BOTTOM

                setDrawGridLines(false)
                setDrawLabels(true)
                setCenterAxisLabels(true)

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "-${(24F - value).roundToInt()}h"
                    }
                }
            }
        }
    }

    private fun setCandleChart() {
        candleChart.apply {
            description.isEnabled = false
            legend.isEnabled = false

            activity?.getColorFromAttr(R.attr.colorOnSurface)?.let { setNoDataTextColor(it) }
            activity?.getColorFromAttr(R.attr.colorSurface)?.let { setBackgroundColor(it) }

            setDrawGridBackground(false)
            setDrawBorders(false)

            xAxis.apply {
                labelCount = 6
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawLabels(true)
                setCenterAxisLabels(true)

                textColor = activity?.getColorFromAttr(R.attr.colorOnSurface) ?: Color.BLACK
            }

            axisLeft.apply {
                textColor = activity?.getColorFromAttr(R.attr.colorOnSurface) ?: Color.BLACK
                setDrawGridLines(false)
                setDrawLabels(false)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            }
        }
    }

    private fun plotLineGraphic(data1: ArrayList<Entry>) {
        val chartDataSets = arrayListOf<ILineDataSet>()

        val set1 = makeLineSet(data1, "Set 1")
        chartDataSets.add(set1)

        lineChart.data = LineData(chartDataSets)
        lineChart.invalidate()
    }

    private fun plotCandleGraphic(data: ArrayList<CandleEntry>, label: String) {
        if (data.isNotEmpty()) {
            val candleDataSet = makeCandleSet(data, label)

            candleChart.data = CandleData(candleDataSet)
            candleChart.invalidate()
        }
    }

    private fun makeLineSet(data: ArrayList<Entry>, label: String): LineDataSet {
        val set = LineDataSet(data, label)

        set.apply {
            setDrawIcons(false)
            setDrawCircleHole(false)
            setDrawCircles(false)
            setDrawFilled(true)

            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)
            color = activity?.getColorFromAttr(R.attr.colorOnSecondary) ?: Color.BLACK
            lineWidth = 2F
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2F
        }
        return set
    }

    private fun makeCandleSet(data: ArrayList<CandleEntry>, label: String): CandleDataSet {
        return CandleDataSet(data, label).apply {
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
    }

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun getFormatTimeWithTZ(currentTime: Date): String {
        val timeZoneDate = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeZoneDate.format(currentTime)
    }
}
