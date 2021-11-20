package com.avelycure.cryptostats.di

import com.avelycure.cryptostats.presentation.home.CryptoInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        CryptoInfoViewModel(
            homeInteractors = get()
        )
    }
}