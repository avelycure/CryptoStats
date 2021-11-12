package com.avelycure.cryptostats.di

import android.content.Context
import com.avelycure.cryptostats.common.Constants
import com.avelycure.cryptostats.data.remote.api_service.GeminiApiService
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import com.avelycure.cryptostats.utils.network_utils.NetworkStatus
import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.presentation.CryptoInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.avelycure.cryptostats.data.local.AppDatabase

import androidx.room.Room


val appModule = module {
    single<ICryptoRepo> { CryptoRepo(get(), get(), get()) }

    single<INetworkStatus> { NetworkStatus(get()) }

    fun provideAppDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "database")
            //.allowMainThreadQueries()
            //.fallbackToDestructiveMigration()
            .build()

    fun provideScreenDao(appDatabase: AppDatabase) = appDatabase.screenDao()

    single { provideAppDatabase(get()) }
    single { provideScreenDao(get()) }

    viewModel { CryptoInfoViewModel(get()) }

    single<GeminiApiService> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(GeminiApiService::class.java)
    }
}