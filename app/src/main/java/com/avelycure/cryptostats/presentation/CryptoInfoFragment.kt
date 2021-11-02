package com.avelycure.cryptostats.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.avelycure.cryptostats.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class CryptoInfoFragment: Fragment() {

    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crypto_info, container, false)
        lineChart = view.findViewById(R.id.chart)

        val data:ArrayList<Entry> = arrayListOf(
            Entry(0F, 20F),
            Entry(1F, 24F),
            Entry(2F, 2F),
            Entry(3F, 10F)
        )

        val dataSet = LineDataSet(data, "Data set 1")
        dataSet.setDrawIcons(false)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawFilled(true)

        val dataSets: ArrayList<ILineDataSet> = arrayListOf(
            dataSet
        )

        val lineData: LineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()

        return view
    }
}