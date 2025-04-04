package com.renatovaler.globantchallenge.presentation.ui.search

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.domain.usecase.getAll.GetAllCountriesUseCase
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCase
import com.renatovaler.globantchallenge.utils.CountryFactory
import com.renatovaler.globantchallenge.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val testDispatchers get() = dispatcherRule.testDispatchers

    private val getAllCountriesUseCase: GetAllCountriesUseCase = mock()
    private val searchCountriesUseCase: SearchCountriesUseCase = mock()
    private lateinit var viewModel: SearchViewModel

    private val peru = CountryFactory.peru()
    private val guyana = CountryFactory.guyana()
    private val allCountries = listOf(peru, guyana)
    private val searchResult = listOf(peru)

    @Before
    fun setup() {
        whenever(getAllCountriesUseCase()).thenReturn(flowOf(Result.success(allCountries)))
        whenever(searchCountriesUseCase("per")).thenReturn(flowOf(Result.success(searchResult)))
        whenever(searchCountriesUseCase("xzy")).thenReturn(flowOf(Result.success(emptyList())))
        whenever(searchCountriesUseCase("error")).thenReturn(flowOf(Result.failure(NetworkError.Unknown)))
        viewModel = SearchViewModel(getAllCountriesUseCase, searchCountriesUseCase, testDispatchers)
    }

    @Test
    fun `GIVEN initial state WHEN no query is entered THEN show all countries`() = runTest {
        advanceUntilIdle()

        viewModel.state.test {
            skipItems(1)
            val final = awaitItem()

            assertThat(final.query).isEmpty()
            assertThat(final.results.map { it.commonName }).containsExactly("Peru", "Guyana")
            assertThat(final.isLoading).isFalse()
            assertThat(final.error).isNull()
        }
    }

    @Test
    fun `GIVEN valid query WHEN result is found THEN emit country list`() = runTest {
        viewModel.onIntent(SearchIntent.OnQueryChanged("per"))
        advanceUntilIdle()

        viewModel.state.test {
            skipItems(2)
            val final = awaitItem()

            assertThat(final.query).isEqualTo("per")
            assertThat(final.results.map { it.commonName }).containsExactly("Peru")
            assertThat(final.isLoading).isFalse()
            assertThat(final.error).isNull()
        }
    }

    @Test
    fun `GIVEN query with no result WHEN search is performed THEN show loading state`() = runTest {
        viewModel.onIntent(SearchIntent.OnQueryChanged("xzy"))
        advanceUntilIdle()

        viewModel.state.test {
            skipItems(1)
            val item = awaitItem()
            assertThat(item.query).isEqualTo("xzy")
            assertThat(item.results).isEmpty()
            assertThat(item.isLoading).isTrue()
            assertThat(item.error).isNull()
        }
    }

    @Test
    fun `GIVEN search fails WHEN query is entered THEN show error`() = runTest {
        whenever(searchCountriesUseCase("per")).thenReturn(
            flowOf(Result.failure(NetworkError.Timeout))
        )

        viewModel.onIntent(SearchIntent.OnQueryChanged("per"))
        advanceTimeBy(300)
        runCurrent()

        viewModel.state.test {
            skipItems(2)
            val item = awaitItem()
            assertThat(item.results).isEmpty()
            assertThat(item.error).isEqualTo("Tiempo de espera agotado")
        }
    }


    @Test
    fun `GIVEN getAllCountries fails WHEN ViewModel is created THEN show error`() = runTest {
        whenever(getAllCountriesUseCase()).thenReturn(flowOf(Result.failure(NetworkError.ServerError)))

        val viewModel =
            SearchViewModel(getAllCountriesUseCase, searchCountriesUseCase, testDispatchers)

        viewModel.state.test {
            skipItems(1)
            val item = awaitItem()

            assertThat(item.error).isEqualTo("Error del servidor")
        }
    }

    @Test
    fun `GIVEN unknown exception WHEN search is triggered THEN show unknown error`() = runTest {
        viewModel.onIntent(SearchIntent.OnQueryChanged("error"))
        advanceTimeBy(300)

        viewModel.state.test {
            skipItems(2)
            val item = awaitItem()
            assertThat(item.results).isEmpty()
            assertThat(item.error).isEqualTo("Error desconocido")
        }
    }
}
