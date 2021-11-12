package com.avelycure.cryptostats.utils.network_utils

import io.reactivex.rxjava3.core.Observable

interface INetworkStatus {
    fun isOnline(): Observable<Boolean>
}