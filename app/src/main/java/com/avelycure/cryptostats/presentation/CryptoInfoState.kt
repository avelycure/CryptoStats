package com.avelycure.cryptostats.presentation

import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Statistic24h
import com.avelycure.cryptostats.domain.Ticker

data class CryptoInfoState(
    val statistic: Statistic24h,
    val coinPrice: CoinPrice,
    val ticker: Ticker
)