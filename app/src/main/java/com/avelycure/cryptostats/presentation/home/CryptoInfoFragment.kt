package com.avelycure.cryptostats.presentation.home

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.domain.models.CoinPrice
import com.avelycure.cryptostats.domain.models.Statistic24h
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
    private lateinit var tvActuality: AppCompatTextView
    private lateinit var rvTrades: RecyclerView

    private val cryptoInfoViewModel: CryptoInfoViewModel by viewModel()
    private lateinit var adapter: TradeAdapter

    private var coin = Constants.DEFAULT_COIN_SYMBOL
    private var currency = Constants.DEFAULT_CURRENCY_SYMBOL
    private var timeFrame = Constants.DEFAULT_TIME_FRAME

    private lateinit var coinSpinner: AppCompatSpinner
    private lateinit var coinSpinnerAdapter: ArrayAdapter<String>

    private lateinit var currencySpinner: AppCompatSpinner
    private lateinit var currencySpinnerAdapter: ArrayAdapter<String>

    private fun fetchData() {
        cryptoInfoViewModel.requestData(
            CryptoInfoViewModel.RequestParameters(
                symbol = "$coin$currency",
                limit = 50,
                timeFrame = timeFrame,
                pair = (coin + currency).uppercase()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)

        initViews(view)

        swipeRefresh.setOnRefreshListener {
            cryptoInfoViewModel.clear()
            fetchData()
            swipeRefresh.isRefreshing = false
        }

        cryptoInfoViewModel.state.observe(viewLifecycleOwner, { state ->

            if (state.remoteData)
                tvActuality.text = "actual"
            else
                tvActuality.text = "cached"

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

                currentTvAskPrice.text = state.tickerV2.ask.toString()
                currentTvBidPrice.text = state.tickerV2.bid.toString()
            }

            updatePrice(state.coinPrice)

            adapter.tradeList = state.trades
            rvTrades.adapter?.notifyDataSetChanged()
        })

        if (savedInstanceState == null) {
            fetchData()
            cryptoInfoViewModel.firstStart = false
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        cryptoInfoViewModel.clear()
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

        rvTrades = view.findViewById(R.id.rv_trades)
        setRv()

        tvCoinValue = view.findViewById(R.id.ci_tv_coin_value)
        tvPercentageChanging24h = view.findViewById(R.id.ci_tv_percent_change_in_last_24h)
        tvLowest24h = view.findViewById(R.id.ci_tv_lowest_in_last_24h)
        tvHighest24h = view.findViewById(R.id.ci_tv_highest_in_last_24h)
        tvOpenPrice = view.findViewById(R.id.ci_tv_open_price)
        tvPriceChange = view.findViewById(R.id.ci_tv_price_change)
        currentTvBidPrice = view.findViewById(R.id.ci_tv_current_price_bid)
        currentTvAskPrice = view.findViewById(R.id.ci_tv_current_ask)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_layout)
        tvActuality = view.findViewById(R.id.data_actuality)
        coinSpinner = view.findViewById(R.id.coin_spinner)
        currencySpinner = view.findViewById(R.id.currency_spinner)

        coinSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!cryptoInfoViewModel.firstStart) {
                    cryptoInfoViewModel.clear()
                    coin = Constants.COIN_SYMBOL.filterValues {
                        it == parent?.getItemAtPosition(position).toString()
                    }.keys.first()
                    fetchData()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!cryptoInfoViewModel.firstStart) {
                    cryptoInfoViewModel.clear()
                    currency = parent?.getItemAtPosition(position).toString()
                    fetchData()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        coinSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Constants.COIN_SYMBOL.values.toList()
        )
        coinSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        coinSpinner.adapter = coinSpinnerAdapter
        coinSpinner.setSelection(coinSpinnerAdapter.getPosition(Constants.DEFAULT_COIN))

        currencySpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Constants.CURRENCY_SYMBOL.values.toList()
        )
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = currencySpinnerAdapter
        currencySpinner.setSelection(currencySpinnerAdapter.getPosition(Constants.DEFAULT_CURRENCY))

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.ci_toolbar))
        (activity as AppCompatActivity).supportActionBar?.title = "Crypto stats"
    }

    private fun setRv() {
        adapter = TradeAdapter(cryptoInfoViewModel.state.value?.trades ?: emptyList())
        rvTrades.adapter = adapter
        rvTrades.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setLineChart() {
        lineChart.apply {
            isScaleXEnabled = false
            isDragEnabled = false
            isScaleYEnabled = false

            axisLeft.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM

            setDrawGridBackground(false)
            setPinchZoom(false)

            activity?.getColorFromAttr(R.attr.colorSurface)?.let { setBackgroundColor(it) }
            activity?.getColorFromAttr(R.attr.colorOnSurface)?.let { setNoDataTextColor(it) }

            axisRight.apply {
                textColor = activity?.getColorFromAttr(R.attr.colorOnSurface) ?: Color.BLACK

                setDrawGridLines(true)
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
            setDrawValues(false)

            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)
            color = activity?.getColorFromAttr(R.attr.colorOnSecondary) ?: Color.BLACK
            lineWidth = 2F
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.1F
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
