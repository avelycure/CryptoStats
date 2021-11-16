package com.avelycure.cryptostats.common

object Constants {
    const val BASE_URL = "https://api.gemini.com/"

    val COIN_SYMBOL = mapOf(
        "btc" to "Bitcoin",
        "eth" to "Ethereum",
    )

    val CURRENCY_SYMBOL = mapOf(
        "$" to "usd",
        "€" to "eur",
    )

    const val DEFAULT_COIN = "Bitcoin"
    const val DEFAULT_CURRENCY = "usd"

    const val DEFAULT_COIN_SYMBOL = "btc"
    const val DEFAULT_CURRENCY_SYMBOL = "usd"

    const val DEFAULT_TIME_FRAME = "1m"
}