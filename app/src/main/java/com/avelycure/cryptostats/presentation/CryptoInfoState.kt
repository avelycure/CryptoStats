package com.avelycure.cryptostats.presentation

import com.avelycure.cryptostats.domain.CoinPrice
import com.avelycure.cryptostats.domain.Statistic24h

data class CryptoInfoState(
    val statistic: Statistic24h,
    val coinPrice: CoinPrice
)