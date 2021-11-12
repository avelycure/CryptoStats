package com.avelycure.cryptostats.presentation

import com.avelycure.cryptostats.domain.models.CoinPrice
import com.avelycure.cryptostats.domain.models.Statistic24h
import com.avelycure.cryptostats.domain.models.Ticker
import com.avelycure.cryptostats.domain.models.Trade

data class CryptoInfoState(
    val statistic: Statistic24h,
    val coinPrice: CoinPrice,
    val ticker: Ticker,
    val trades: List<Trade>
)