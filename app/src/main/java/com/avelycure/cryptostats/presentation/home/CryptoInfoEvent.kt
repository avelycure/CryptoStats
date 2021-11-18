package com.avelycure.cryptostats.presentation.home

sealed class CryptoInfoEvent {
    object OnRemoveHeadFromQueue: CryptoInfoEvent()
}
