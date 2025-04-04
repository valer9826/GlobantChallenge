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
        val dtoList = CountryDtoFactory.allCountries()
        whenever(api.getAll()).thenReturn(dtoList)

        val result = repository.getAll().first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
        assertThat(result.getOrNull()?.map { it.commonName }).containsExactly("Peru", "Grenada").inOrder()
    }

    @Test
    fun `GIVEN successful API response WHEN search is called with 'per' THEN emit success with countries including Peru`() = runTest {
        val query = "per"
        val dtoList = CountryDtoFactory.searchCountries()
        whenever(api.search(query)).thenReturn(dtoList)

        val result = repository.search(query).first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.first()?.commonName).isEqualTo("Peru")
    }

    @Test
    fun `GIVEN API throws exception WHEN getAll is called THEN emit failure result`() = runTest {
        whenever(api.getAll()).thenThrow(RuntimeException("Network error"))

        val result = repository.getAll().first()

        result.assertFailureOfType<NetworkError.Unknown>()
    }

    @Test
    fun `GIVEN SocketTimeoutException WHEN search is called THEN emit failure with NetworkError Timeout`() = runTest {
        whenever(api.search("any")).thenThrow(SocketTimeoutException("Read timed out"))

        val result = repository.search("any").first()

        result.assertFailureOfType<NetworkError.Timeout>()
    }

    @Test
    fun `GIVEN IOException WHEN search is called THEN emit failure with NetworkError NoInternetConnection`() = runTest {
        whenever(api.search("any")).thenThrow(IOException("No connection"))

        val result = repository.search("any").first()

        result.assertFailureOfType<NetworkError.NoInternetConnection>()
    }

    @Test
    fun `GIVEN HttpException 400 WHEN search is called THEN emit failure with NetworkError ClientError`() = runTest {
        val exception = HttpException(Response.error<Any>(400, "".toResponseBody("application/json".toMediaType())))
        whenever(api.search("any")).thenThrow(exception)

        val result = repository.search("any").first()

        result.assertFailureOfType<NetworkError.ClientError>()
    }

    @Test
    fun `GIVEN HttpException 500 WHEN search is called THEN emit failure with NetworkError ServerError`() = runTest {
        val exception = HttpException(Response.error<Any>(500, "".toResponseBody("application/json".toMediaType())))
        whenever(api.search("any")).thenThrow(exception)

        val result = repository.search("any").first()

        result.assertFailureOfType<NetworkError.ServerError>()
    }

    @Test
    fun `GIVEN unknown exception WHEN search is called THEN emit failure with NetworkError Unknown`() = runTest {
        whenever(api.search("any")).thenThrow(IllegalStateException("Unknown error"))

        val result = repository.search("any").first()

        result.assertFailureOfType<NetworkError.Unknown>()
    }
}
