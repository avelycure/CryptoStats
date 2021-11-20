package com.avelycure.cryptostats

import com.avelycure.cryptostats.data.remote.models.*
import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.interactors.*
import com.avelycure.cryptostats.domain.state.DataState
import com.avelycure.cryptostats.utils.network_utils.INetworkStatus
import com.avelycure.cryptostats.utils.network_utils.NetworkStatus
import io.reactivex.rxjava3.core.Observable
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class InteractorsRemoteTest {
    @Mock
    lateinit var getCandles: GetCandles

    @Mock
    lateinit var getCoinPrice: GetCoinPrice

    @Mock
    lateinit var getTickerV2: GetTickerV2

    @Mock
    lateinit var getTickerV1: GetTickerV1

    @Mock
    lateinit var getTrades: GetTrades

    @Mock
    lateinit var repo: ICryptoRepo

    @Mock
    lateinit var networkStatus: INetworkStatus
    private var setUpIsDone = false

    @Before
    fun setUp() {
        if (!setUpIsDone) {
            repo = mock<CryptoRepo> {
                on { getCandlesFromRemote("btcusd", "1m") } doReturn Observable.fromCallable {
                    listOf<List<Float>>(
                        listOf(
                            1f, 2f, 3f, 4f, 5f
                        ),
                        listOf(
                            6f, 7f, 8f, 9f, 10f
                        )
                    )
                }
                on { getPriceFeedFromRemote() } doReturn Observable.fromCallable {
                    listOf<ResponsePriceFeed>(
                        ResponsePriceFeed(
                            "BTCUSD",
                            "1000",
                            "5"
                        )
                    )
                }
                on { getTickerV1FromRemote("btcusd") } doReturn Observable.fromCallable {
                    ResponseTickerV1(
                        10f, 20f, 30f, ResponseVolumeBtcUsd(40f, 50f, 60L)
                    )
                }
                on { getTickerV2FromRemote("btcusd") } doReturn Observable.fromCallable {
                    ResponseTickerV2(
                        "btcusd", 20f, 30f, 15f,
                        17f, emptyList(), 90f, 100f
                    )
                }
                on { getTradesFromRemote("btcusd", 50) } doReturn Observable.fromCallable {
                    listOf<ResponseTradeHistory>(
                        ResponseTradeHistory(
                            100L, 100L, 100L, 4f, 6f, "gemini", "buy"
                        )
                    )
                }
            }

            networkStatus = mock<NetworkStatus> {
                on { isOnline() } doReturn Observable.fromCallable {
                    true
                }
            }

            getCandles = GetCandles(repo, networkStatus)
            getCoinPrice = GetCoinPrice(repo, networkStatus)
            getTickerV1 = GetTickerV1(repo, networkStatus)
            getTickerV2 = GetTickerV2(repo, networkStatus)
            getTrades = GetTrades(repo, networkStatus)

            setUpIsDone = true
        }
    }

    @Test
    fun shouldReturnRemoteData() {
        getCandles.execute("btcusd", "1m").subscribe {
            assertThat(it, instanceOf(DataState.DataRemote::class.java))
        }
        getCoinPrice.execute().subscribe {
            assertThat(it, instanceOf(DataState.DataRemote::class.java))
        }
        getTrades.execute("btcusd", 50).subscribe {
            assertThat(it, instanceOf(DataState.DataRemote::class.java))
        }
        getTickerV1.execute("btcusd").subscribe {
            assertThat(it, instanceOf(DataState.DataRemote::class.java))
        }
        getTickerV2.execute("btcusd").subscribe {
            assertThat(it, instanceOf(DataState.DataRemote::class.java))
        }
    }
}