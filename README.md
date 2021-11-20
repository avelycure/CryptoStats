# CryptoStats

App for getting information from Gemini Api:
* Coin value
* Trades
* Candles
* 24h coin statistic
* 24h high, low, open, close value of the coin

## Project features
* Kotlin
* [[RXJava3]](https://github.com/ReactiveX/RxJava) + [[RxAndroid]](https://github.com/ReactiveX/RxAndroid) for making async work
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

## Charts
Currency changes can be seen on the charts that were built using the [[MPAndroid library]](https://github.com/PhilJay/MPAndroidChart). Changes over 24 hours are presented using a line graph. 

Candles are represented using a candlestick chart
