package com.renatovaler.globantchallenge.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renatovaler.globantchallenge.data.remote.error.NetworkError
import com.renatovaler.globantchallenge.domain.usecase.getAll.GetAllCountriesUseCase
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCase
import com.renatovaler.globantchallenge.presentation.ui.search.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    getAllCountriesUseCase: GetAllCountriesUseCase,
    searchCountriesUseCase: SearchCountriesUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val _searchResults = _query
        .debounce(200)
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            searchCountriesUseCase(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.success(emptyList()))

    private val _allCountries = getAllCountriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.success(emptyList()))

    val state: StateFlow<SearchState> = combine(
        _query,
        _searchResults,
        _allCountries
    ) { query, searchResult, allCountriesResult ->

        val isInitialLoad = query.isBlank() && allCountriesResult.isSuccess && allCountriesResult.getOrNull().isNullOrEmpty()
        val isSearching = query.length >= 2 && searchResult.isSuccess && searchResult.getOrNull().isNullOrEmpty()

        val error = searchResult.exceptionOrNull()
            ?: allCountriesResult.exceptionOrNull()

        val countries = when {
            query.isBlank() || query.length < 2 -> allCountriesResult.getOrNull().orEmpty()
            else -> searchResult.getOrNull().orEmpty()
        }.map { it.toUiModel() }

        SearchState(
            query = query,
            results = countries,
            isLoading = isInitialLoad || isSearching,
            error = error?.let { mapErrorToMessage(it) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchState()
    )

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.OnQueryChanged -> _query.value = intent.query
            is SearchIntent.OnCountryClicked -> {}
        }
    }

    private fun mapErrorToMessage(error: Throwable): String {
        return when (error) {
            is NetworkError.NoInternetConnection -> "Sin conexión a Internet"
            is NetworkError.ServerError -> "Error del servidor"
            is NetworkError.ClientError -> "Error en la petición"
            is NetworkError.Timeout -> "Tiempo de espera agotado"
            else -> "Error desconocido"
        }
    }
}


