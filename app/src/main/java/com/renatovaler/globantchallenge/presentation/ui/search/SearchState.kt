package com.renatovaler.globantchallenge.presentation.ui.search

import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel

data class SearchState(
    val query: String = "",
    val results: List<CountryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)