package com.avelycure.cryptostats.presentation

import androidx.lifecycle.ViewModel
import com.avelycure.cryptostats.data.repo.ICryptoRepo

class CryptoInfoViewModel(
    val repo: ICryptoRepo
): ViewModel() {

    fun getHello() = repo.hello()
}