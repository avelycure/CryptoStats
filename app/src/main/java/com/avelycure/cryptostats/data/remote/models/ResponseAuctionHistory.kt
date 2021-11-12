package com.avelycure.cryptostats.data.remote.models

data class ResponseAuctionHistory(
    val timestamp: Long,
    val timestampms: Long,
    val auction_id: Long,
    val eid: Long,
    val event_type: String,
    val auction_result: String,
    val auction_price: Float,
    val auction_quantity: Float,
    val highest_bid_price: Float,
    val lowest_ask_price: Float,
    val collar_price: Float
)
