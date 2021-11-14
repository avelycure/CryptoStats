package com.avelycure.cryptostats.presentation.home

import com.avelycure.cryptostats.domain.models.CoinPrice
import com.avelycure.cryptostats.domain.models.Statistic24h
import com.avelycure.cryptostats.domain.models.TickerV2
import com.avelycure.cryptostats.domain.models.Trade
import com.avelycure.cryptostats.domain.state.Queue
import com.avelycure.cryptostats.domain.state.UIComponent

data class CryptoInfoState(
    val statistic: Statistic24h,
    val coinPrice: CoinPrice,
    val tickerV2: TickerV2,
    val trades: List<Trade>,
    val remoteData: Boolean,
    val errorQueue: Queue<UIComponent>,
)