package com.avelycure.cryptostats.presentation.home

import com.avelycure.cryptostats.domain.interactors.*

data class HomeInteractors(
    val getTickerV1: GetTickerV1,
    val getTickerV2: GetTickerV2,
    val getCandles: GetCandles,
    val getCoinPrice: GetCoinPrice,
    val getTrades: GetTrades,
    val prepareCandles: PrepareCandles
)