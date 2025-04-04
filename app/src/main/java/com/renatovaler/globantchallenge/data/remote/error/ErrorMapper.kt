package com.renatovaler.globantchallenge.data.remote.error

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun mapToNetworkError(e: Throwable): NetworkError {
    return when {
        e is SocketTimeoutException || e.findCause<SocketTimeoutException>() != null -> NetworkError.Timeout
        e is IOException || e.findCause<IOException>() != null -> NetworkError.NoInternetConnection
        e is HttpException -> when (e.code()) {
            in 400..499 -> NetworkError.ClientError
            in 500..599 -> NetworkError.ServerError
            else -> NetworkError.Unknown
        }
        else -> NetworkError.Unknown
    }
}

inline fun <reified T : Throwable> Throwable.findCause(): T? {
    var current: Throwable? = this
    while (current != null) {
        if (current is T) return current
        current = current.cause
    }
    return null
}


