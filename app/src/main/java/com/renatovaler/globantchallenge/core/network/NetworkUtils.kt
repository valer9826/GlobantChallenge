package com.renatovaler.globantchallenge.core.network

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
        e is SocketTimeoutException || e.findCause<SocketTimeoutException>() != null ->
            NetworkError.Timeout

        e is UnknownHostException || e.findCause<UnknownHostException>() != null ->
            NetworkError.NoInternetConnection

        e is ConnectException || e.findCause<ConnectException>() != null ->
            NetworkError.NoInternetConnection

        e is IOException || e.findCause<IOException>() != null ->
            NetworkError.NoInternetConnection

        e is HttpException || e.findCause<HttpException>() != null -> {
            val http = e as? HttpException ?: e.findCause<HttpException>()
            when (http?.code()) {
                in 400..499 -> NetworkError.ClientError
                in 500..599 -> NetworkError.ServerError
                else -> NetworkError.Unknown
            }
        }

        else -> NetworkError.Unknown
    }
}

inline fun <reified T : Throwable> Throwable.findCause(): T? {
    var cause: Throwable? = this
    while (cause != null) {
        if (cause is T) return cause
        cause = cause.cause
    }
    return null
}
