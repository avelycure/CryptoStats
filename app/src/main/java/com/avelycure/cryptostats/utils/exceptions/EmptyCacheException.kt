package com.avelycure.cryptostats.utils.exceptions

class EmptyCacheException(
    message: String = "No cache data, turn on the Internet"
) : Exception(message)