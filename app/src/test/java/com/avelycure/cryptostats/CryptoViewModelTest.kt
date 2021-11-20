package com.avelycure.cryptostats

import io.reactivex.rxjava3.core.Observable
import org.junit.runner.RunWith
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.avelycure.cryptostats.domain.interactors.*
import com.avelycure.cryptostats.domain.models.*
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.presentation.home.CryptoInfoViewModel
import com.avelycure.cryptostats.domain.interactors.HomeInteractors
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CryptoViewModelTest {
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var homeInteractors: HomeInteractors

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

    lateinit var cryptoInfoViewModel: CryptoInfoViewModel

    @Before
    fun setUp() {
        homeInteractors = mock<HomeInteractors>()

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

        getTrades = mock<GetTrades> {
            on { execute("btcusd", 50) } doReturn Observable.just(
                DataState.DataRemote(
                    data = listOf(
                        Trade(
                            timestampms = 1000L,
                            tid = 70L,
                            price = 100f,
                            amount = 10000f,
                            type = "sell"
                        ),
                        Trade(
                            timestampms = 1200L,
                            tid = 700L,
                            price = 200f,
                            amount = 300f,
                            type = "buy"
                        )
                    )
                )
            )
        }

        prepareCandles = mock<PrepareCandles> {
            on {
                execute(
                    listOf(
                        Candle(
                            time = 10f,
                            open = 100f,
                            high = 100f,
                            low = 100f,
                            close = 100f
                        )
                    )
                )
            } doReturn Observable.just(
                listOf(
                    Candle(
                        time = 10f,
                        open = 100f,
                        high = 101f,
                        low = 99f,
                        close = 30f
                    ),
                    Candle(
                        time = 11f,
                        open = 101f,
                        high = 102f,
                        low = 98f,
                        close = 40f
                    )
                )
            )
        }

        getTickerV2 = mock<GetTickerV2> {
            on { execute("btcusd") } doReturn Observable.just(
                DataState.DataRemote(
                    data = TickerV2(
                        symbol = "btcusd",
                        bid = 99f,
                        ask = 101f,
                        open = 80f,
                        high = 110f,
                        low = 90f,
                        close = 100f,
                        changes = listOf(50f, 50f, 50f)
                    )
                )
            )
        }

        getCoinPrice = mock<GetCoinPrice> {
            on { execute() } doReturn Observable.just(
                DataState.DataRemote(
                    data = listOf(
                        CoinPrice(
                            price = "100",
                            percentChange24h = "10",
                            pair = "BTCUSD"
                        )
                    )
                )
            )
        }

        getCandles = mock<GetCandles> {
            on { execute(symbol = "btcusd", timeFrame = "1m") } doReturn Observable.just(
                DataState.DataRemote(
                    data = listOf(
                        Candle(
                            time = 10f,
                            open = 100f,
                            high = 100f,
                            low = 100f,
                            close = 100f
                        )
                    )
                )
            )
        }

        whenever(homeInteractors.getTickerV2).thenReturn(getTickerV2)
        whenever(homeInteractors.getCandles).thenReturn(getCandles)
        whenever(homeInteractors.getCoinPrice).thenReturn(getCoinPrice)
        whenever(homeInteractors.getTrades).thenReturn(getTrades)
        whenever(homeInteractors.prepareCandles).thenReturn(prepareCandles)
        whenever(homeInteractors.getTickerV1).thenReturn(getTickerV1)

        cryptoInfoViewModel = CryptoInfoViewModel(homeInteractors)
    }

    @Test
    fun shouldReturnTickerV1() {
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

    @Test
    fun shouldReturnTickerV2() {
        cryptoInfoViewModel.requestData(
            CryptoInfoViewModel.RequestParameters(
                "btcusd",
                "1m",
                50,
                "BTCUSD"
            )
        )
        val result = cryptoInfoViewModel.state.value!!.statistic.high
        assertThat(result, equalTo(110f))
    }

    @Test
    fun shouldReturnPreparedCandles() {
        cryptoInfoViewModel.requestData(
            CryptoInfoViewModel.RequestParameters(
                "btcusd",
                "1m",
                50,
                "BTCUSD"
            )
        )
        val result = cryptoInfoViewModel.state.value!!.statistic.candles[0]
        assertThat(result.time, equalTo(10f))
    }

    @Test
    fun shouldReturnTrades() {
        cryptoInfoViewModel.requestData(
            CryptoInfoViewModel.RequestParameters(
                "btcusd",
                "1m",
                50,
                "BTCUSD"
            )
        )
        val result = cryptoInfoViewModel.state.value!!.trades[0]
        assertThat(result.amount, equalTo(10000f))
    }

    @Test
    fun shouldReturnPriceFeed() {
        cryptoInfoViewModel.requestData(
            CryptoInfoViewModel.RequestParameters(
                "btcusd",
                "1m",
                50,
                "BTCUSD"
            )
        )
        val result = cryptoInfoViewModel.state.value!!.coinPrice
        assertThat(result.price, equalTo("100"))
    }
}