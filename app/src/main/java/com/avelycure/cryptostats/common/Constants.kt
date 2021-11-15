package com.avelycure.cryptostats.common

object Constants {
    const val BASE_URL = "https://api.gemini.com/"

    val coinSymbol = mapOf(
        "btc" to "Bitcoin",
        "eth" to "Ethereum",
        "doge" to "Dogecoin",
    )

    val moneySymbol = mapOf(
        "usd" to "$",
        "eur" to "€",
    )
}