package com.renatovaler.globantchallenge.data.remote.error

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun mapToNetworkError(e: Throwable): NetworkError {
    return when (e) {
        is SocketTimeoutException -> NetworkError.Timeout
        is IOException -> NetworkError.NoInternetConnection
        is HttpException -> when (e.code()) {
            in 400..499 -> NetworkError.ClientError
            in 500..599 -> NetworkError.ServerError
            else -> NetworkError.Unknown
        }
        else -> NetworkError.Unknown
    }
}

