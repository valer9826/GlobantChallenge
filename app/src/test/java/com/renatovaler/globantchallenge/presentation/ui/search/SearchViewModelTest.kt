package com.renatovaler.globantchallenge.presentation.ui.search

import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCase
import com.renatovaler.globantchallenge.utils.CountryFactory
import com.renatovaler.globantchallenge.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: CountryRepository = mock()
    private val searchCountriesUseCase: SearchCountriesUseCase = mock()
    private lateinit var viewModel: SearchViewModel

    private val peru = CountryFactory.peru()
    private val guyana = CountryFactory.guyana()
    private val allCountries = listOf(peru, guyana)
    private val searchResult = listOf(peru)

    @Before
    fun setup() {
        whenever(repository.getAllCountries()).thenReturn(flowOf(Result.success(allCountries)))
        whenever(searchCountriesUseCase("per")).thenReturn(flowOf(Result.success(searchResult)))
        whenever(searchCountriesUseCase("xzy")).thenReturn(flowOf(Result.success(emptyList())))
        whenever(searchCountriesUseCase("error")).thenReturn(flowOf(Result.failure(NetworkError.Unknown)))

        viewModel = SearchViewModel(repository, searchCountriesUseCase)
    }

    @Test
    fun `GIVEN loading initial data WHEN no query is entered THEN show all countries`() = runTest {
        // WHEN
        advanceUntilIdle()

        // THEN
        val final = withTimeout(2000) {
            viewModel.state.first { it.results.isNotEmpty() }
        }
        assertThat(final.query).isEmpty()
        assertThat(final.results.map { it.commonName }).containsExactly("Peru", "Guyana")
        assertThat(final.isLoading).isFalse()
        assertThat(final.error).isNull()
    }

    @Test
    fun `GIVEN valid query WHEN result is found THEN emit country list`() = runTest {
        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("per"))
        advanceTimeBy(300)
        runCurrent()

        val final = viewModel.state.first { it.query == "per" && it.results.isNotEmpty() }
        assertThat(final.results.map { it.commonName }).containsExactly("Peru")
        assertThat(final.isLoading).isFalse()
        assertThat(final.error).isNull()
    }

    @Test
    fun `GIVEN valid query with no result WHEN search is performed THEN show loading state`() = runTest {
        viewModel.onIntent(SearchIntent.OnQueryChanged("xzy"))
        advanceUntilIdle()

        // THEN
        val final = withTimeout(2000) {
            viewModel.state.first { it.query == "xzy" && it.isLoading }
        }

        assertThat(final.query).isEqualTo("xzy")
        assertThat(final.results).isEmpty()
        assertThat(final.isLoading).isTrue()
        assertThat(final.error).isNull()
    }

    @Test
    fun `GIVEN search fails WHEN query is entered THEN show Timeout error`() = runTest {
        whenever(searchCountriesUseCase("per")).thenReturn(flowOf(Result.failure(NetworkError.Timeout)))

        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("per"))
        advanceTimeBy(300)
        runCurrent()

        // THEN
        val final = withTimeout(2000) {
            viewModel.state.first { it.query == "per" && it.error != null }
        }

        assertThat(final.results).isEmpty()
        assertThat(final.error).isEqualTo("Timeout error")
    }

    @Test
    fun `GIVEN getAllCountries fails WHEN ViewModel is created THEN show Server error`() = runTest {
        whenever(repository.getAllCountries()).thenReturn(flowOf(Result.failure(NetworkError.ServerError)))

        val viewModel = SearchViewModel(repository, searchCountriesUseCase)

        // THEN
        val final = withTimeout(2000) {
            viewModel.state.first { it.error != null }
        }

        assertThat(final.error).isEqualTo("Server error")
    }

    @Test
    fun `GIVEN unknown exception WHEN search is triggered THEN show unknown error`() = runTest {
        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("error"))
        advanceTimeBy(300)
        runCurrent()

        // THEN
        val final = withTimeout(2000) {
            viewModel.state.first { it.query == "error" && it.error != null }
        }

        assertThat(final.results).isEmpty()
        assertThat(final.error).isEqualTo("Unknown error")
    }
}