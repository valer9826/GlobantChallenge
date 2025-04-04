package com.renatovaler.globantchallenge.core.network

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        if (!NetworkHelper.isNetworkAvailable()) {
            Result.failure(NetworkError.NoInternetConnection)
        } else {
            Result.success(apiCall())
        }
    } catch (e: Exception) {
        Result.failure(mapToNetworkError(e))
    }
}

fun mapToNetworkError(e: Throwable): NetworkError {
    return when {
        e is SocketTimeoutException || e.findCause<SocketTimeoutException>() != null -> NetworkError.Timeout
        e is IOException || e.findCause<IOException>() != null -> NetworkError.NoInternetConnection
        e is HttpException || e.findCause<HttpException>() != null -> {
            val http = e as? HttpException ?: e.findCause<HttpException>()!!
            when (http.code()) {
                in 400..499 -> NetworkError.ClientError
                in 500..599 -> NetworkError.ServerError
                else -> NetworkError.Unknown
            }
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
