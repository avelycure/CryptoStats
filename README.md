# CryptoStats

App for getting information from [Gemini API](https://docs.gemini.com/rest-api/):
* Coin value
* Trades
* Candles
* 24h coin statistic
* 24h high, low, open, close value of the coin

## Project features

* 100% Kotlin
* Functional & Reactive Programming with [RXJava3](https://github.com/ReactiveX/RxJava) + [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* Dependency injection with [Koin](https://insert-koin.io/) 
* Retrofit to fetch data from the [Gemini API](https://docs.gemini.com/rest-api/)
* Cache local data with [Room Persistence Library](https://developer.android.com/training/data-storage/room)
* MPAndroid to plot nice charts
* Modern architecture (Clean Architecture, Model-View-ViewModel)
* Instrumental tests with [Espresso](https://developer.android.com/training/testing/espresso)
* Unit test with JUnit4 and Mockito
* Firebase: Crashlytics, GoogleAnalytics

## Functionality

The entire application fits on one screen. It has different types of statistics for the selected cryptocurrency obtained from the Gemini API.

<p>
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/actual1.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/actual2.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/actual3.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/cache1.jpg" width="256" />
</p>

## Caching

According to the principles of Clean Architecture, data is handled on the Data Layer. There is a [CryptoRepo](https://github.com/avelycure/CryptoStats/blob/master/app/src/main/java/com/avelycure/cryptostats/data/repo/CryptoRepo.kt) class that can execute requests to the Gemini server using [Retrofit](https://github.com/avelycure/CryptoStats/blob/master/app/src/main/java/com/avelycure/cryptostats/data/remote/api_service/GeminiApiService.kt) and can save the result to the [Room](https://github.com/avelycure/CryptoStats/blob/master/app/src/main/java/com/avelycure/cryptostats/data/local/dao/CacheDao.kt) database. The work of determining from which source to receive data is performed by [interactors](https://github.com/avelycure/CryptoStats/tree/master/app/src/main/java/com/avelycure/cryptostats/domain/interactors)

## Charts

Currency changes can be seen on the charts that were built using the [MPAndroid library](https://github.com/PhilJay/MPAndroidChart). Changes over 24 hours are presented using a line graph. 

<p>
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/linechart.jpg" width="256" />
</p>

Candles are represented using a candlestick chart

<p>
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/candles.jpg" width="256" />
</p>

## Handling internet connection
To work with internet connection there is a [NetworkStatus](https://github.com/avelycure/CryptoStats/blob/master/app/src/main/java/com/avelycure/cryptostats/utils/network_utils/NetworkStatus.kt) class. If the user launches the app with internet connection everything is all right. But if the internet connection is lost user will get an error. Also if the user did not enabled the internet before first launch of the app, he will get an error that there is no cache data. 

<p>
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/error.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/on_disable_network.jpg" width="256" />
</p>

## Refresh data

If the user got an [EmptyCacheException](https://github.com/avelycure/CryptoStats/blob/master/app/src/main/java/com/avelycure/cryptostats/utils/exceptions/EmptyCacheException.kt) he can reload data with onSwipeRefresh
<p>
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/on_update.jpg" width="256" />
</p>
