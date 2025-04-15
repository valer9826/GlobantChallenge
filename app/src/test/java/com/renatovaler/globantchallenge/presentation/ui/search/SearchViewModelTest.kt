package com.renatovaler.globantchallenge.presentation.ui.search

import com.google.common.truth.Truth.assertThat
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import com.renatovaler.globantchallenge.utils.CountryFactory
import com.renatovaler.globantchallenge.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
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
    private lateinit var viewModel: SearchViewModel

    private val peru = CountryFactory.peru()
    private val guyana = CountryFactory.guyana()
    private val allCountries = listOf(peru, guyana)
    private val searchResult = listOf(peru)

    @Before
    fun setup() {
        // GIVEN
        whenever(repository.getAllCountries()).thenReturn(flowOf(Result.success(allCountries)))
        whenever(repository.search("per")).thenReturn(flowOf(Result.success(searchResult)))
        whenever(repository.search("xzy")).thenReturn(flowOf(Result.success(emptyList())))
        whenever(repository.search("error")).thenReturn(flowOf(Result.failure(NetworkError.Unknown)))

        viewModel = SearchViewModel(repository)
    }

    @Test
    fun `GIVEN loading initial data WHEN no query is entered THEN show all countries`() = runTest {
        // WHEN
        val result = withTimeout(2000) {
            viewModel.state.first { it.results.isNotEmpty() }
        }

        // THEN
        assertThat(result.query).isEmpty()
        assertThat(result.results.map { it.commonName }).containsExactly("Peru", "Guyana")
        assertThat(result.isLoading).isFalse()
        assertThat(result.error).isNull()
    }

    @Test
    fun `GIVEN valid query WHEN result is found THEN emit country list`() = runTest {
        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("per"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "per" && it.results.isNotEmpty() }
        }

        // THEN
        assertThat(result.query).isEqualTo("per")
        assertThat(result.results.map { it.commonName }).containsExactly("Peru")
        assertThat(result.isLoading).isFalse()
        assertThat(result.error).isNull()
    }

    @Test
    fun `GIVEN valid query with no result WHEN search is performed THEN show loading state`() = runTest {
        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("xzy"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "xzy" && it.isLoading }
        }

        // THEN
        assertThat(result.query).isEqualTo("xzy")
        assertThat(result.results).isEmpty()
        assertThat(result.isLoading).isTrue()
        assertThat(result.error).isNull()
    }

    @Test
    fun `GIVEN search fails WHEN query is entered THEN show Timeout error`() = runTest {
        // GIVEN
        whenever(repository.search("per")).thenReturn(flowOf(Result.failure(NetworkError.Timeout)))

        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("per"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "per" && it.error != null }
        }

        // THEN
        assertThat(result.results).isEmpty()
        assertThat(result.error).isEqualTo("Timeout error")
    }

    @Test
    fun `GIVEN getAllCountries fails WHEN ViewModel is created THEN show Server error`() = runTest {
        // GIVEN
        whenever(repository.getAllCountries()).thenReturn(flowOf(Result.failure(NetworkError.ServerError)))

        // WHEN
        val viewModel = SearchViewModel(repository)

        val result = withTimeout(2000) {
            viewModel.state.first { it.error != null }
        }

        // THEN
        assertThat(result.error).isEqualTo("Server error")
    }

    @Test
    fun `GIVEN unknown exception WHEN search is triggered THEN show unknown error`() = runTest {
        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("error"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "error" && it.error != null }
        }

        // THEN
        assertThat(result.results).isEmpty()
        assertThat(result.error).isEqualTo("Unknown error")
    }

    @Test
    fun `GIVEN query 'guy' WHEN search is called THEN return only Guyana`() = runTest {
        // GIVEN
        val resultList = listOf(guyana)
        whenever(repository.search("guy")).thenReturn(flowOf(Result.success(resultList)))

        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("guy"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "guy" && it.results.size == 1 }
        }

        // THEN
        assertThat(result.results.map { it.commonName }).containsExactly("Guyana")
        assertThat(result.error).isNull()
        assertThat(result.isLoading).isFalse()
    }

    @Test
    fun `GIVEN query 'xx' WHEN search is called THEN return empty list`() = runTest {
        // GIVEN
        whenever(repository.search("xx")).thenReturn(flowOf(Result.success(emptyList())))

        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("xx"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "xx" && it.results.isEmpty() }
        }

        // THEN
        assertThat(result.results).isEmpty()
        assertThat(result.error).isNull()
        assertThat(result.isLoading).isTrue()
    }

    @Test
    fun `GIVEN query with less than 2 letters WHEN search is triggered THEN ignore search and show all countries`() = runTest {
        // WHEN
        viewModel.onIntent(SearchIntent.OnQueryChanged("p"))
        advanceTimeBy(300)
        runCurrent()

        val result = withTimeout(2000) {
            viewModel.state.first { it.query == "p" }
        }

        // THEN
        assertThat(result.results.map { it.commonName }).containsExactly("Peru", "Guyana")
        assertThat(result.isLoading).isFalse()
        assertThat(result.error).isNull()
    }

}
