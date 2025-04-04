package com.renatovaler.globantchallenge.data.repository

import com.renatovaler.globantchallenge.MainDispatcherRule
import com.renatovaler.globantchallenge.data.dto.CarDto
import com.renatovaler.globantchallenge.data.dto.CoatOfArmsDto
import com.renatovaler.globantchallenge.data.dto.CountryDto
import com.renatovaler.globantchallenge.data.dto.CurrencyDto
import com.renatovaler.globantchallenge.data.dto.FlagsDto
import com.renatovaler.globantchallenge.data.dto.NameDto
import com.renatovaler.globantchallenge.data.remote.api.CountriesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.mock
import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.data.remote.error.NetworkError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.Test

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
    fun `GIVEN successful API response WHEN getAll is called THEN emit success result with mapped countries`() =
        runTest {
            // GIVEN
            val dtoList = listOf(
                CountryDto(
                    name = NameDto(common = "Peru", official = "Republic of Peru"),
                    capital = listOf("Lima"),
                    region = "Americas",
                    subregion = "South America",
                    flags = FlagsDto("https://flag.png"),
                    coatOfArms = null,
                    population = 33000000L,
                    languages = mapOf("spa" to "Spanish"),
                    currencies = mapOf("PEN" to CurrencyDto("Peruvian sol", "S/")),
                    car = CarDto(side = "right")
                ),
                CountryDto(
                    name = NameDto(common = "Grenada", official = "Grenada"),
                    capital = listOf("St. George's"),
                    region = "Americas",
                    subregion = "Caribbean",
                    flags = FlagsDto(png = "https://flagcdn.com/w320/gd.png"),
                    coatOfArms = CoatOfArmsDto(
                        png = "https://mainfacts.com/media/images/coats_of_arms/gd.png",
                    ),
                    population = 112519,
                    languages = mapOf("eng" to "English"),
                    currencies = mapOf(
                        "XCD" to CurrencyDto(
                            name = "Eastern Caribbean dollar",
                            symbol = "$"
                        )
                    ),
                    car = CarDto(side = "left")
                )
            )
            whenever(api.getAll()).thenReturn(dtoList)

            // WHEN
            val result = repository.getAll().first()

            // THEN
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).hasSize(2)
            assertThat(result.getOrNull()?.map { it.commonName })
                .containsExactly("Peru", "Grenada")
                .inOrder()
        }

    @Test
    fun `GIVEN API throws exception WHEN getAll is called THEN emit failure result`() = runTest {
        // GIVEN
        whenever(api.getAll()).thenThrow(RuntimeException("Network error"))

        // WHEN
        val result = repository.getAll().first()

        // THEN
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError.Unknown::class.java)
    }

    @Test
    fun `GIVEN successful API response WHEN search is called with 'per' THEN emit success with countries including Peru`() =
        runTest {
            // GIVEN
            val query = "per"
            val dtoList = listOf(
                CountryDto(
                    name = NameDto(common = "Peru", official = "Republic of Peru"),
                    capital = listOf("Lima"),
                    region = "Americas",
                    subregion = "South America",
                    flags = FlagsDto("https://flag.png"),
                    coatOfArms = null,
                    population = 33000000L,
                    languages = mapOf("spa" to "Spanish"),
                    currencies = mapOf("PEN" to CurrencyDto("Peruvian sol", "S/")),
                    car = CarDto(side = "right")
                ),
                CountryDto(
                    name = NameDto(common = "Guyana", official = "Co-operative Republic of Guyana"),
                    capital = listOf("Georgetown"),
                    region = "Americas",
                    subregion = "South America",
                    flags = FlagsDto(png = "https://flagcdn.com/w320/gy.png"),
                    coatOfArms = CoatOfArmsDto(png = "https://mainfacts.com/media/images/coats_of_arms/gy.png"),
                    population = 786559,
                    languages = mapOf("eng" to "English"),
                    currencies = mapOf(
                        "GYD" to CurrencyDto(
                            name = "Guyanese dollar",
                            symbol = "$"
                        )
                    ),
                    car = CarDto(side = "left")
                )
            )
            whenever(api.search(query)).thenReturn(dtoList)

            // WHEN
            val result = repository.search(query).first()

            // THEN
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isNotEmpty()
            assertThat(result.getOrNull()?.first()?.commonName).isEqualTo("Peru")
        }

    @Test
    fun `GIVEN API throws exception WHEN search is called THEN emit failure result with NetworkError`() =
        runTest {
            // GIVEN
            whenever(api.search("any")).thenThrow(RuntimeException("Read timed out"))

            // WHEN
            val result = repository.search("any").first()

            // THEN
            assertThat(result.isFailure).isTrue()
            val exception = result.exceptionOrNull()
            assertThat(exception).isInstanceOf(NetworkError.Unknown::class.java)
        }

    @Test
    fun `GIVEN SocketTimeoutException WHEN search is called THEN emit failure with NetworkError Timeout`() =
        runTest {
            // GIVEN
            whenever(api.search("any")).thenAnswer {
                throw RuntimeException(SocketTimeoutException("Read timed out"))
            }

            // WHEN
            val result = repository.search("any").first()

            // THEN
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(NetworkError.Timeout)

        }


    @Test
    fun `GIVEN IOException WHEN search is called THEN emit failure with NetworkError NoInternetConnection`() =
        runTest {
            // GIVEN
            whenever(api.search("any")).thenAnswer {
                throw RuntimeException(IOException("No connection")) //
            }

            // WHEN
            val result = repository.search("any").first()

            // THEN
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(NetworkError.NoInternetConnection)
        }

    @Test
    fun `GIVEN HttpException 400 WHEN search is called THEN emit failure with NetworkError ClientError`() =
        runTest {
            // GIVEN
            val exception = HttpException(
                Response.error<Any>(
                    400,
                    "".toResponseBody("application/json".toMediaType())
                )
            )
            whenever(api.search("any")).thenThrow(exception)

            // WHEN
            val result = repository.search("any").first()

            // THEN
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(NetworkError.ClientError)
        }

    @Test
    fun `GIVEN HttpException 500 WHEN search is called THEN emit failure with NetworkError ServerError`() =
        runTest {
            // GIVEN
            val exception = HttpException(
                Response.error<Any>(
                    500,
                    "".toResponseBody("application/json".toMediaType())
                )
            )
            whenever(api.search("any")).thenThrow(exception)

            // WHEN
            val result = repository.search("any").first()

            // THEN
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(NetworkError.ServerError)
        }

    @Test
    fun `GIVEN unknown exception WHEN search is called THEN emit failure with NetworkError Unknown`() =
        runTest {
            // GIVEN
            whenever(api.search("any")).thenThrow(IllegalStateException("Some unknown error"))

            // WHEN
            val result = repository.search("any").first()

            // THEN
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(NetworkError.Unknown)
        }

}
