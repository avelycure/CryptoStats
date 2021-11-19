package com.avelycure.cryptostats.presentation.home

import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.interactors.*
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus

data class HomeInteractors(
    val getTickerV1: GetTickerV1,
    val getTickerV2: GetTickerV2,
    val getCandles: GetCandles,
    val getCoinPrice: GetCoinPrice,
    val getTrades: GetTrades,
    val prepareCandles: PrepareCandles
)