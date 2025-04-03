package com.renatovaler.globantchallenge.presentation.ui.search

import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel

sealed class SearchIntent {
    data class OnQueryChanged(val query: String) : SearchIntent()
    data class OnCountryClicked(val countryUiModel: CountryUiModel) : SearchIntent()
}