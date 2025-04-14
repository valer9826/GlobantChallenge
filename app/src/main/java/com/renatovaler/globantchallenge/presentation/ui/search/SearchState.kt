package com.renatovaler.globantchallenge.presentation.ui.search

import androidx.compose.runtime.Immutable
import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SearchState(
    val query: String = "",
    val results: ImmutableList<CountryUiModel> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)