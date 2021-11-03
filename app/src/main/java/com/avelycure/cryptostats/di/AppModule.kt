package com.avelycure.cryptostats.di

import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.presentation.CryptoInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ICryptoRepo> {CryptoRepo()}

    viewModel { CryptoInfoViewModel(get()) }
}