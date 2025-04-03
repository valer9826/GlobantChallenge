package com.renatovaler.globantchallenge.presentation.ui.search

import com.renatovaler.globantchallenge.domain.model.Country

data class SearchState(
    val query: String = "",
    val results: List<Country> = emptyList(),
    val isLoading: Boolean = false
)