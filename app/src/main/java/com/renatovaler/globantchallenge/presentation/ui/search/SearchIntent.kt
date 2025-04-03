package com.renatovaler.globantchallenge.presentation.ui.search

import com.renatovaler.globantchallenge.domain.model.Country

sealed class SearchIntent {
    data class OnQueryChanged(val query: String) : SearchIntent()
    data class OnCountryClicked(val country: Country) : SearchIntent()
}