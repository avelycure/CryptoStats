# CryptoStats

App for getting information from Gemini Api:
* Coin value
* Trades
* Candles
* 24h coin statistic
* 24h high, low, open, close value of the coin

## Project features
* Kotlin
* [RXJava3](https://github.com/ReactiveX/RxJava) + [RxAndroid](https://github.com/ReactiveX/RxAndroid) for making async work
* Koin for dependecy injection
* Retrofit to fetch data from API
* Room to cache data
* MPAndroid to plot nice charts
* CleanArchitecture
* MVVM
* Instrumental tests with Espresso
* Unit test with JUnit4 and Mockito

## Functionality
The entire application fits on one screen. It has different types of statistics for the selected cryptocurrency obtained from the Gemini API.
<p>
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/actual1.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/actual2.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/actual3.jpg" width="256" />
  <img src="https://github.com/avelycure/avelycure/blob/master/assets/cryptostats/cache1.jpg" width="256" />
</p>

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
