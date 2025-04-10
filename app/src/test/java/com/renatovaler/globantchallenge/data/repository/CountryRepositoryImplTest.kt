package com.renatovaler.globantchallenge.data.repository

import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.utils.MainDispatcherRule
import com.renatovaler.globantchallenge.data.remote.api.CountriesApi
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.utils.CountryDtoFactory
import com.renatovaler.globantchallenge.utils.assertFailureOfType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class CountryRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api: CountriesApi = mock()
    private lateinit var repository: CountryRepositoryImpl

    @Before
    fun setup() {
        repository = CountryRepositoryImpl(api)
    }

    @Test
    fun `GIVEN successful API response WHEN getAll is called THEN emit success result with mapped countries`() = runTest {
        // GIVEN
        val dtoList = CountryDtoFactory.allCountries()
        whenever(api.getAll()).thenReturn(dtoList)

        // WHEN
        val result = repository.getAll().first()

        // THEN
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
        assertThat(result.getOrNull()?.map { it.commonName }).containsExactly("Peru", "Grenada").inOrder()
    }

    @Test
    fun `GIVEN successful API response WHEN search is called with 'per' THEN emit success with countries including Peru`() = runTest {
        // GIVEN
        val query = "per"
        val dtoList = CountryDtoFactory.searchCountries()
        whenever(api.search(query)).thenReturn(dtoList)

        // WHEN
        val result = repository.search(query).first()

        // THEN
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.first()?.commonName).isEqualTo("Peru")
    }

    @Test
    fun `GIVEN API throws runtime exception WHEN getAll is called THEN emit failure result with Unknown error`() = runTest {
        // GIVEN
        whenever(api.getAll()).thenThrow(RuntimeException("Network error"))

        // WHEN
        val result = repository.getAll().first()

        // THEN
        result.assertFailureOfType<NetworkError.Unknown>()
    }

    @Test
    fun `GIVEN SocketTimeoutException WHEN search is called THEN emit failure with NetworkError Timeout`() = runTest {
        // GIVEN
        whenever(api.search("any")).thenThrow(RuntimeException(SocketTimeoutException("Read timed out")))

        // WHEN
        val result = repository.search("any").first()

        // THEN
        result.assertFailureOfType<NetworkError.Timeout>()
    }

    @Test
    fun `GIVEN IOException WHEN search is called THEN emit failure with NetworkError NoInternetConnection`() = runTest {
        // GIVEN
        whenever(api.search("any")).thenThrow(RuntimeException(IOException("No connection")))

        // WHEN
        val result = repository.search("any").first()

        // THEN
        result.assertFailureOfType<NetworkError.NoInternetConnection>()
    }

    @Test
    fun `GIVEN HttpException 400 WHEN search is called THEN emit failure with NetworkError ClientError`() = runTest {
        // GIVEN
        val exception = HttpException(Response.error<Any>(400, "".toResponseBody("application/json".toMediaType())))
        whenever(api.search("any")).thenThrow(exception)

        // WHEN
        val result = repository.search("any").first()

        // THEN
        result.assertFailureOfType<NetworkError.ClientError>()
    }

    @Test
    fun `GIVEN HttpException 500 WHEN search is called THEN emit failure with NetworkError ServerError`() = runTest {
        // GIVEN
        val exception = HttpException(Response.error<Any>(500, "".toResponseBody("application/json".toMediaType())))
        whenever(api.search("any")).thenThrow(exception)

        // WHEN
        val result = repository.search("any").first()

        // THEN
        result.assertFailureOfType<NetworkError.ServerError>()
    }

    @Test
    fun `GIVEN unknown exception WHEN search is called THEN emit failure with NetworkError Unknown error`() = runTest {
        // GIVEN
        whenever(api.search("any")).thenThrow(IllegalStateException("Unknown error"))

        // WHEN
        val result = repository.search("any").first()

        // THEN
        result.assertFailureOfType<NetworkError.Unknown>()
    }
}