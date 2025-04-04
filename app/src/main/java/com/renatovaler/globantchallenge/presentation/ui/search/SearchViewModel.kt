package com.renatovaler.globantchallenge.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renatovaler.globantchallenge.core.utils.DispatcherProvider
import com.renatovaler.globantchallenge.core.network.NetworkError
import com.renatovaler.globantchallenge.domain.usecase.getAll.GetAllCountriesUseCase
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCase
import com.renatovaler.globantchallenge.presentation.ui.search.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
    searchCountriesUseCase: SearchCountriesUseCase,
    dispatchers: DispatcherProvider,
) : ViewModel() {

    internal var scope = viewModelScope

    constructor(
        getAllCountriesUseCase: GetAllCountriesUseCase,
        searchCountriesUseCase: SearchCountriesUseCase,
        dispatchers: DispatcherProvider,
        testScope: CoroutineScope
    ) : this(getAllCountriesUseCase, searchCountriesUseCase, dispatchers) {
        this.scope = testScope
    }

    private val _query = MutableStateFlow("")

    private val _searchResults = _query
        .debounce(200)
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            searchCountriesUseCase(query)
                .catch { emit(Result.failure(it)) }
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), Result.success(emptyList()))

    private val _allCountries = getAllCountriesUseCase()
        .catch { e ->
            println("🔥 getAllCountriesUseCase lanzó: $e")
            emit(Result.failure(e))
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), Result.success(emptyList()))

    val state: StateFlow<SearchState> = combine(
        _query,
        _searchResults,
        _allCountries
    ) { query, searchResult, allCountriesResult ->
        println("🌍 _allCountries: success=${allCountriesResult.isSuccess}, exception=${allCountriesResult.exceptionOrNull()}")
        val isInitialLoad = query.isBlank() && allCountriesResult.isSuccess && allCountriesResult.getOrNull().isNullOrEmpty()
        val isSearching = query.length >= 2 && searchResult.isSuccess && searchResult.getOrNull().isNullOrEmpty()

        val error = (searchResult.exceptionOrNull()
            ?: allCountriesResult.exceptionOrNull()).let { mapErrorToMessage(it) }

        val countries = when {
            query.isBlank() || query.length < 2 -> allCountriesResult.getOrNull().orEmpty()
            else -> searchResult.getOrNull().orEmpty()
        }.map { it.toUiModel() }

        SearchState(
            query = query,
            results = countries,
            isLoading = isInitialLoad || isSearching,
            error = error
        )
    }.stateIn(
        scope = scope,
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
            is NetworkError.NoInternetConnection -> "Sin conexión a Internet"
            is NetworkError.ServerError -> "Error del servidor"
            is NetworkError.ClientError -> "Error en la petición"
            is NetworkError.Timeout -> "Tiempo de espera agotado"
            is NetworkError.Unknown -> "Error desconocido"
            else -> null
        }
    }
}