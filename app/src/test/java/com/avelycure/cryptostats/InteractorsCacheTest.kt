package com.avelycure.cryptostats

import com.avelycure.cryptostats.data.local.entities.*
import com.avelycure.cryptostats.data.remote.models.*
import com.avelycure.cryptostats.data.repo.CryptoRepo
import com.avelycure.cryptostats.data.repo.ICryptoRepo
import com.avelycure.cryptostats.domain.interactors.*
import com.avelycure.cryptostats.domain.models.Candle
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
class InteractorsCacheTest {
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
                on { getCandlesFromCache() } doReturn EntityCandles(
                    0, 0
                ).apply {
                    candles = listOf(
                        EntitySmallCandle(
                            0, 0, 3f, 10f, 15f, 9f, 11f
                        ),
                        EntitySmallCandle(
                            1, 0, 4f, 11f, 14f, 10f, 11f
                        )
                    )
                }
                on { getPriceFeedFromCache() } doReturn listOf(
                    EntityPriceFeed(
                        0, 0, "BTCUSD", "101", "3"
                    )
                )
                on { getTickerV1FromCache() } doReturn EntityTickerV1(
                    0, 0, 15f, 20f
                )
                on { getTickerV2FromCache() } doReturn EntityTickerV2(
                    0, 0, "btcusd", 10f, 11f, 12f, 13f, 14f, 15f,  emptyList<Float>()
                )
                on { getTradesFromCache() } doReturn listOf(
                    EntityTradeHistory(
                        0,0,10L,300,1000f, 400f, "buy"
                    )
                )
            }

            networkStatus = mock<NetworkStatus> {
                on { isOnline() } doReturn Observable.fromCallable {
                    false
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
    fun shouldReturnCacheData() {
        getCandles.execute("btcusd", "1m").subscribe {
            assertThat(it, instanceOf(DataState.DataCache::class.java))
        }
        getCoinPrice.execute().subscribe {
            assertThat(it, instanceOf(DataState.DataCache::class.java))
        }
        getTrades.execute("btcusd", 50).subscribe {
            assertThat(it, instanceOf(DataState.DataCache::class.java))
        }
        getTickerV1.execute("btcusd").subscribe {
            assertThat(it, instanceOf(DataState.DataCache::class.java))
        }
        getTickerV2.execute("btcusd").subscribe {
            assertThat(it, instanceOf(DataState.DataCache::class.java))
        }
    }
}