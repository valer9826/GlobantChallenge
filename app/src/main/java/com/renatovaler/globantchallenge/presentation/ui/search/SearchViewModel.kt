package com.renatovaler.globantchallenge.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renatovaler.globantchallenge.domain.model.Country
import com.renatovaler.globantchallenge.domain.usecase.getAll.GetAllCountriesUseCase
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val searchCountriesUseCase: SearchCountriesUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _searchResults = _query
        .debounce(400)
        .filter { it.isNotBlank() }
        .flatMapLatest { name ->
            searchCountriesUseCase(name)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _allCountries = MutableStateFlow<List<Country>>(emptyList())
        .onStart { getAllCountriesUseCase() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val state: StateFlow<SearchState> = combine(
        _query,
        _searchResults,
        _allCountries
    ) { query, searchResults, allCountries ->
        SearchState(
            query = query,
            results = when {
                query.isBlank() -> allCountries
                query.length < 2 -> allCountries // <- Lógica UI: no cambiar resultados si input incompleto
                else -> searchResults
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchState()
    )

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.OnQueryChanged -> _query.value = intent.query
            is SearchIntent.OnCountryClicked -> { /* Navegación */
            }
        }
    }
}

