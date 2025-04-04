package com.renatovaler.globantchallenge.domain.usecase.getAll

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.utils.MainDispatcherRule
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import com.renatovaler.globantchallenge.utils.CountryFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllCountriesUseCaseImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CountryRepository = mock()
    private lateinit var useCase: GetAllCountriesUseCaseImpl

    @Before
    fun setup() {
        useCase = GetAllCountriesUseCaseImpl(repository)
    }

    @Test
    fun `GIVEN successful result with countries WHEN UseCase is invoked THEN emit success`() = runTest {
        // GIVEN
        val expectedCountries = CountryFactory.someCountries()
        whenever(repository.getAll()).thenReturn(flowOf(Result.success(expectedCountries)))

        // WHEN & THEN
        useCase().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(expectedCountries)
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN successful result with empty list WHEN UseCase is invoked THEN emit empty success`() = runTest {
        // GIVEN
        whenever(repository.getAll()).thenReturn(flowOf(Result.success(emptyList())))

        // WHEN & THEN
        useCase().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN failure result WHEN UseCase is invoked THEN emit failure`() = runTest {
        // GIVEN
        val error = NetworkError.ServerError
        whenever(repository.getAll()).thenReturn(flowOf(Result.failure(error)))

        // WHEN & THEN
        useCase().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(error)
            awaitComplete()
        }
    }
}
