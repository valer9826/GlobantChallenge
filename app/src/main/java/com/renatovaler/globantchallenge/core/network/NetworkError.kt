package com.renatovaler.globantchallenge.core.network

sealed class NetworkError : Throwable() {
    object NoInternetConnection : NetworkError()
    object ServerError : NetworkError()
    object ClientError : NetworkError()
    object Timeout : NetworkError()
    object Unknown : NetworkError()
}