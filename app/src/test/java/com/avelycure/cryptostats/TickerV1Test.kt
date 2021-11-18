package com.avelycure.cryptostats

import io.reactivex.rxjava3.core.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.interactors.*
import com.avelycure.cryptostats.domain.models.TickerV1
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.presentation.home.CryptoInfoState
import com.avelycure.cryptostats.presentation.home.CryptoInfoViewModel
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import com.avelycure.cryptostats.utils.network_utils.NetworkStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class TickerV1Test {
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkStatusEnabled: INetworkStatus

    @Mock
    lateinit var repo: ICryptoRepo

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var getTickerV1: GetTickerV1

    @Mock
    lateinit var getCandles: GetCandles

    @Mock
    lateinit var getCoinPrice: GetCoinPrice

    @Mock
    lateinit var getTickerV2: GetTickerV2

    @Mock
    lateinit var getTrades: GetTrades

    @Mock
    lateinit var prepareCandles: PrepareCandles

    @Mock
    lateinit var observer: Observer<CryptoInfoState>

    lateinit var cryptoInfoViewModel: CryptoInfoViewModel

    @Before
    fun setUp() {
        repo = Mockito.mock(CryptoRepo::class.java)
        networkStatusEnabled = Mockito.mock(NetworkStatus::class.java)

        getTickerV1 = mock<GetTickerV1> {
            on { execute("btcusd") } doReturn Observable.just(
                DataState.DataRemote(
                    data = TickerV1(
                        bid = 100f,
                        ask = 1000f
                    )
                )
            )
        }
        getTickerV2 = GetTickerV2(repo, networkStatusEnabled)
        getCandles = GetCandles(repo, networkStatusEnabled)
        getTrades = GetTrades(repo, networkStatusEnabled)
        getCoinPrice = GetCoinPrice(repo, networkStatusEnabled)
        prepareCandles = PrepareCandles()

        cryptoInfoViewModel = CryptoInfoViewModel(
            getCandles,
            getTickerV2,
            getCoinPrice,
            getTickerV1,
            getTrades,
            prepareCandles
        )
    }

    @Test
    fun shouldReturnTickerV1() {

        cryptoInfoViewModel.state.observeForever(observer!!)

        cryptoInfoViewModel.requestData(
            CryptoInfoViewModel.RequestParameters(
                "btcusd",
                "1m",
                50,
                "BTCUSD"
            )
        )

        val result = cryptoInfoViewModel.state.value!!.tickerV2.bid
        assertThat(result, equalTo(100f))
    }
}