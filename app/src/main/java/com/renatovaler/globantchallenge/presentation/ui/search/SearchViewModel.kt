package com.renatovaler.globantchallenge.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.domain.model.Country
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import com.renatovaler.globantchallenge.presentation.ui.search.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    repository: CountryRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private var isFirstTimeInitialLoadingData = true
    private var cachedCountriesResult: Result<List<Country>> = Result.success(emptyList())

    private val _searchResults = _query
        .debounce(200)
        .flatMapLatest { query ->
            if (query.length >= 2) {
                repository.search(query)
                    .catch { emit(Result.failure(it)) }
            } else {
                flowOf(Result.success(emptyList()))
            }
        }

    private val _allCountries: Flow<Result<List<Country>>> = flow {
        if (isFirstTimeInitialLoadingData) {
            isFirstTimeInitialLoadingData = false

            repository.getAllCountries()
                .catch { emit(Result.failure(it)) }
                .collect {
                    cachedCountriesResult = it
                    emit(it)
                }
        } else {
            emit(cachedCountriesResult)
        }
    }

    val state: StateFlow<SearchState> = combine(
        _query,
        _searchResults,
        _allCountries
    ) { query, searchResult, allCountriesResult ->
        val isInitialLoad = query.isBlank() && allCountriesResult.isSuccess && allCountriesResult.getOrNull().isNullOrEmpty()
        val isSearching = query.length >= 2 && searchResult.isSuccess && searchResult.getOrNull().isNullOrEmpty()

        val error = (searchResult.exceptionOrNull()
            ?: allCountriesResult.exceptionOrNull()).let { mapErrorToMessage(it) }

        val countries = when {
            query.isBlank() || query.length < 2 -> allCountriesResult.getOrNull().orEmpty()
            else -> searchResult.getOrNull().orEmpty()
        }.map { it.toUiModel() }.toPersistentList()

        SearchState(
            query = query,
            results = countries,
            isLoading = isInitialLoad || isSearching,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchState(isLoading = true)
    )

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.OnQueryChanged -> _query.value = intent.query
            is SearchIntent.OnCountryClicked -> {}
        }
    }

    private fun mapErrorToMessage(error: Throwable?): String? {
        return when (error) {
            is NetworkError.NoInternetConnection -> "No internet connection"
            is NetworkError.ServerError -> "Server error"
            is NetworkError.ClientError -> "Request error"
            is NetworkError.Timeout -> "Timeout error"
            is NetworkError.Unknown -> "Unknown error"
            else -> null
        }
    }
}