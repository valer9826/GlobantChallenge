package com.renatovaler.globantchallenge.domain.usecase.search

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.utils.MainDispatcherRule
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import com.renatovaler.globantchallenge.utils.CountryFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchCountriesUseCaseImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CountryRepository = mock()
    private lateinit var useCase: SearchCountriesUseCaseImpl

    @Before
    fun setup() {
        useCase = SearchCountriesUseCaseImpl(repository)
    }

    @Test
    fun `GIVEN successful API response with countries containing 'per' WHEN useCase is invoked THEN emit success with filtered countries`() = runTest {
        // GIVEN
        val query = "per"
        val expectedCountries = CountryFactory.countriesWithNameContainingPer()
        whenever(repository.search(query)).thenReturn(flowOf(Result.success(expectedCountries)))

        // WHEN & THEN
        useCase(query).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(expectedCountries)
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN query with length less than 2 WHEN invoke is called THEN return empty list`() = runTest {
        // GIVEN
        val query = "p"

        // WHEN & THEN
        useCase(query).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN empty query WHEN invoke is called THEN return empty list`() = runTest {
        // GIVEN
        val query = ""

        // WHEN & THEN
        useCase(query).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN repository failure WHEN invoke is called with valid query THEN return failure`() = runTest {
        // GIVEN
        val query = "pe"
        val exception = RuntimeException("Something went wrong")
        whenever(repository.search(query)).thenReturn(flowOf(Result.failure(exception)))

        // WHEN & THEN
        useCase(query).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN valid query with no matching countries WHEN invoke is called THEN return empty list`() = runTest {
        // GIVEN
        val query = "xyz"
        whenever(repository.search(query)).thenReturn(flowOf(Result.success(emptyList())))

        // WHEN & THEN
        useCase(query).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEmpty()
            awaitComplete()
        }
    }
}
