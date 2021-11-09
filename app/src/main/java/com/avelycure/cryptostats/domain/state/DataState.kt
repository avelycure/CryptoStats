package com.avelycure.cryptostats.domain.state

sealed class DataState<T> {
    data class DataRemote<T>(
        val data: T
    ) : DataState<T>()

    data class DataCache<T>(
        val data: T
    ) : DataState<T>()

    data class Response<T>(
        val uiComponent: UIComponent
    ) : DataState<T>()

    data class Loading<T>(
        val progressBarState: ProgressBarState = ProgressBarState.Idle
    ) : DataState<T>()
}

