package com.renatovaler.globantchallenge.presentation.ui.search.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.renatovaler.globantchallenge.presentation.ui.search.SearchIntent
import com.renatovaler.globantchallenge.presentation.ui.search.SearchViewModel
import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun SearchRootScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToDetail: (CountryUiModel) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onIntent = { intent ->
            when (intent) {
                is SearchIntent.OnQueryChanged -> viewModel.onIntent(intent)
                is SearchIntent.OnCountryClicked -> onNavigateToDetail(intent.countryUiModel)
            }
        }
    )
}