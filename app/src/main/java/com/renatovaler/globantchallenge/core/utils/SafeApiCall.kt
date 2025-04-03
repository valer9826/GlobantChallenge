package com.renatovaler.globantchallenge.core.utils

import com.renatovaler.globantchallenge.data.remote.error.mapToNetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
    return try {
        withContext(Dispatchers.IO) {
            Result.success(call())
        }
    } catch (e: Exception) {
        Result.failure(mapToNetworkError(e))
    }
}
